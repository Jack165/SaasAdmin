package com.feng.boot.admin.aspectj;

import com.feng.boot.admin.annotation.ClassDescribe;
import com.feng.boot.admin.annotation.Log;
import com.feng.boot.admin.configuration.properties.BootAdminProperties;
import com.feng.boot.admin.exceptions.BusinessException;
import com.feng.boot.admin.manager.AsyncManager;
import com.feng.boot.admin.manager.factory.AsyncFactory;
import com.feng.boot.admin.project.monitor.operation.model.entity.OperLogEntity;
import com.feng.boot.admin.security.model.User;
import com.feng.boot.admin.security.utils.SecurityUtils;
import com.google.common.collect.Lists;
import com.feng.boot.admin.commons.enums.StatusEnum;
import com.feng.boot.admin.commons.json.utils.Jsons;
import com.feng.boot.admin.commons.lang.ExceptionUtils;
import com.feng.boot.admin.commons.spring.IpUtils;
import com.feng.boot.admin.commons.spring.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 操作日志
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspectj {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspectj.class);
    private final BootAdminProperties properties;

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.feng.boot.admin.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 返回通知
     *
     * @param joinPoint 切点
     * @param result    返回值
     */
    @AfterReturning(returning = "result", pointcut = "logPointCut()")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        handleLog(joinPoint, result, null);
    }

    @Before(value = "logPointCut()")
    public void doBefore() {
        if (properties.isDemoEnabled()) {
            throw new BusinessException("演示环境禁止操作");
        }
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, null, e);
    }

    /**
     * 日志处理
     */
    void handleLog(final JoinPoint joinPoint, Object jsonResult, final Exception e) {
        try {
            Log log = getAnnotationLog(joinPoint);
            if (check(joinPoint, log)) {
                return;
            }
            // 当前用户
            OperLogEntity entity = new OperLogEntity();
            // 状态
            entity.setStatus(StatusEnum.SUCCESS.getValue());
            //操作类型
            entity.setOperType(log.businessType().getValue());
            // 请求用户
            User currentUser = SecurityUtils.getCurrentUser();
            if (null != currentUser) {
                entity.setUsername(currentUser.getUsername());
                entity.setCreateUserId(currentUser.getId());
                entity.setCreateTime(new Date());
            }
            //请求ip
            String ip = IpUtils.getIp(ServletUtils.getRequest());
            entity.setOperIp(ip);
            //描述
            // 类描述
            String controllerDescription = "";
            ClassDescribe classDescribe = joinPoint.getTarget().getClass().getAnnotation(ClassDescribe.class);
            if (null != classDescribe) {
                controllerDescription = classDescribe.value();
            }
            //方法描述
            String controllerMethodDescription = getDescribe(log);
            if (log.controllerApiValue()) {
                entity.setDescription(StringUtils.join(controllerDescription, controllerMethodDescription));
            } else {
                entity.setDescription(controllerMethodDescription);
            }
            // 请求地址
            String requestUrl = ServletUtils.getRequest().getRequestURI();
            entity.setRequestUrl(requestUrl);
            //请求方式
            String requestMethod = ServletUtils.getRequest().getMethod();
            entity.setRequestMethod(requestMethod);
            //操作方法
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            entity.setOperMethod(className + "." + methodName);
            // 请求参数
            if (log.request()) {
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                String requestParamsValue = getRequestValue(joinPoint, request, log.paramsName());
                entity.setRequestParams(requestParamsValue);
            }
            //返回参数
            if (log.response()) {
                String result = Jsons.JSONS.objectToJson(null == jsonResult ? "" : jsonResult);
                entity.setRequestResult(result);
            }
            if (null != e) {
                if (log.requestByError()) {
                    String result = Jsons.JSONS.objectToJson(null == jsonResult ? "" : jsonResult);
                    entity.setRequestResult(result);
                    String message = ExceptionUtils.getExceptionMessage(e);
                    entity.setErrorMessage(StringUtils.substring(message, 0, 2000));
                    entity.setStatus(StatusEnum.FAIL.getValue());
                }
            }
            AsyncManager.me().execute(AsyncFactory.recordOperLog(entity));
        } catch (Exception e1) {
            e1.printStackTrace();
            if (null != e) {
                e.printStackTrace();
            }
            // 记录本地异常日志
            LOGGER.error("==前置通知异常==");
            LOGGER.error("异常信息:{}", e1.getMessage());
            if (null != e) {
                throw new BusinessException(e.getMessage());
            } else {
                throw new BusinessException(e1.getMessage());
            }
        }


    }

    /**
     * 获取请求参数
     *
     * @param joinPoint  切点
     * @param request    请求
     * @param paramsName 参数列表
     * @return 格式化后的请求参数
     */
    String getRequestValue(JoinPoint joinPoint, HttpServletRequest request, String[] paramsName) {
        String requestMethod = request.getMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            Object[] args = joinPoint.getArgs();
            String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            return argsArrayToString(args, parameterNames, paramsName);
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) ServletUtils.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            return StringUtils.substring(paramsMap.toString(), 0, 2000);
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     *
     * @param joinPoint 切点
     * @return {@link Log}注解
     */
    @Nullable
    private Log getAnnotationLog(JoinPoint joinPoint) {
        try {
            Log annotation = null;
            if (joinPoint.getSignature() instanceof MethodSignature) {
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                if (method != null) {
                    annotation = method.getAnnotation(Log.class);
                }
            }
            return annotation;
        } catch (Exception e) {
            LOGGER.warn("获取 {}.{} 的 @Log 注解失败",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), e);
            e.printStackTrace();
            return null;
        }
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        } catch (Exception e) {
            LOGGER.warn("记录操作日志异常", e);
        }
    }

    /**
     * 检查是否不需要记录
     *
     * @param joinPoint 切点
     * @param logAnno   {@link Log}
     * @return true:不需要记录
     */
    private boolean check(JoinPoint joinPoint, Log logAnno) {
        if (null == logAnno || Boolean.FALSE.equals(logAnno.enabled())) {
            return true;
        }
        Log annotation = joinPoint.getTarget().getClass().getAnnotation(Log.class);
        // 不需要记录日志 true
        return null != annotation && !annotation.enabled();
    }

    /**
     * 获取描述
     *
     * @param log {@link Log}
     * @return 描述
     */
    private String getDescribe(Log log) {
        if (null == log) {
            return "";
        }
        return log.value();
    }

    /**
     * 参数拼装
     *
     * @param args             请求参数信息
     * @param parameterNames   请求参数名称
     * @param filterParamsName 需要拦截的参数名称
     * @return 格式化的参数信息
     */
    private String argsArrayToString(Object[] args, String[] parameterNames, String[] filterParamsName) {
        String params = "";
        try {

            if (((parameterNames != null && parameterNames.length > 0)
                    && (filterParamsName != null && filterParamsName.length > 0))) {
                List<Object> paramsObj = Lists.newArrayList();
                for (int i = 0; i < parameterNames.length; i++) {
                    for (String s : filterParamsName) {
                        if (s.equals(parameterNames[i])) {
                            paramsObj.add(args[i]);
                        }
                    }
                }
                params = Jsons.JSONS.objectToJson(paramsObj);
            }
        } catch (Exception e) {
            LOGGER.error("参数拼接异常");
            e.printStackTrace();
        }
        return params;
    }
}
