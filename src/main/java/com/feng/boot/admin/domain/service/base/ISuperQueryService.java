package com.feng.boot.admin.domain.service.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feng.boot.admin.domain.controller.base.IBaseQueryController;
import com.feng.boot.admin.domain.model.dto.BaseDTO;
import com.feng.boot.admin.domain.model.query.BaseParams;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 扩展查询service,针对{@link IBaseQueryController}
 * <pre>
 *     1. 分页查询
 *     2. 列表查询
 * </pre>
 *
 * @param <DTO>    显示层对象类型
 * @param <PARAMS> 请求参数类型
 * @author bing_huang
 * @since 3.0.0
 */
public interface ISuperQueryService<PARAMS extends BaseParams, DTO extends BaseDTO> {
    /**
     * 分页
     *
     * @param params 过滤参数
     * @return 分页列表
     */
    Page<DTO> page(@NonNull PARAMS params);

    /**
     * 列表查询
     *
     * @param params 过滤参数
     * @return 列表
     */
    List<DTO> list(@NonNull PARAMS params);
}
