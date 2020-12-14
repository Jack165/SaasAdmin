package com.feng.boot.admin.domain.controller.base;

import com.feng.boot.admin.domain.service.ISuperBaseService;
import com.feng.boot.admin.domain.model.entity.BaseDomain;

/**
 * 基础 controller<br>
 * <pre>
 * 1. 获取当前业务实体{@link BaseDomain},
 * 2. 获取当前业务Service {@link ISuperBaseService}
 * </pre>
 *
 * @param <ENTITY> 实体类型
 * @author bing_huang
 * @since 3.0.0
 */
@SuppressWarnings({"rawtypes"})
interface IBaseController<ENTITY extends BaseDomain> {
    /**
     * 获取实体的类型
     *
     * @return entity class
     */
    Class<ENTITY> getEntityClass();

    /**
     * 获取service
     *
     * @return service
     */
    ISuperBaseService getBaseService();
}
