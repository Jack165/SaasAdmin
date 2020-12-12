package com.feng.boot.admin.domain.controller.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feng.boot.admin.annotation.ClassDescribe;
import com.feng.boot.admin.annotation.Log;
import com.feng.boot.admin.annotation.PreAuth;
import com.feng.boot.admin.aspectj.LogAspectj;
import com.feng.boot.admin.commons.enums.ResponseStatusEnum;
import com.feng.boot.admin.security.service.PermissionService;
import com.feng.boot.admin.domain.model.dto.BaseDTO;
import com.feng.boot.admin.domain.model.entity.BaseDomain;
import com.feng.boot.admin.domain.model.query.BaseParams;
import com.feng.boot.admin.domain.result.Result;
import com.feng.boot.admin.domain.result.R;
import com.feng.boot.admin.domain.service.ISuperBaseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

/**
 * 基础controller之查询
 * <pre>
 * 1. 实现分页查询{@link #page(BaseParams)}
 * 2. 实现列表查询{@link #list(BaseParams)}
 * 3. 实现共用的权限{@link PreAuthorize}{@link PreAuth}{@link PermissionService}
 * 4. 实现日志的记录{@link Log}{@link ClassDescribe} {@link LogAspectj}
 * </pre>
 *
 * @param <ENTITY> 实体类型
 * @param <ID>     id类型
 * @param <PARAMS> 请求参数类型
 * @param <DTO>    显示层对象类型
 * @author bing_huang
 * @since 3.0.0
 */
public interface IBaseQueryController<ID extends Serializable,
        DTO extends BaseDTO,
        PARAMS extends BaseParams, ENTITY extends BaseDomain> extends IBaseController<ENTITY> {
    /**
     * 分页查询
     *
     * @param params 请求参数
     * @return 分页列表
     */

    @PostMapping("/list/page")
    @SuppressWarnings({"unchecked"})
    @PreAuthorize("@bootAdmin.hasAnyAuthority(this,'ROLE_ADMINISTRATOR','query')")
    default Result<Object> page(@Validated @RequestBody PARAMS params) {
        ISuperBaseService<ID, PARAMS, DTO, ENTITY> service = getBaseService();
        if (null != service) {
            Page<DTO> page = service.page(params);
            return R.success(page);
        }
        return R.result(ResponseStatusEnum.PARAMS_REQUIRED_IS_NULL, "service is null");
    }

    /**
     * 列表查询
     *
     * @param params 请求参数
     * @return 列表
     */
    @PostMapping("/list")
    @SuppressWarnings({"unchecked"})
    default Result<Object> list(@Validated @RequestBody PARAMS params) {
        ISuperBaseService<ID, PARAMS, DTO, ENTITY> service = getBaseService();
        if (null != service) {
            List<DTO> list = service.list(params);
            return R.success(list);
        }
        return R.result(ResponseStatusEnum.PARAMS_REQUIRED_IS_NULL, "service is null");
    }
}
