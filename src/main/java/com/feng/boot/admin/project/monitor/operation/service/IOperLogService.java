package com.feng.boot.admin.project.monitor.operation.service;

import com.feng.boot.admin.project.monitor.operation.model.dto.OperLogDTO;
import com.feng.boot.admin.project.monitor.operation.model.query.OperLogParams;
import com.feng.boot.admin.domain.service.ISuperBaseService;
import com.feng.boot.admin.project.monitor.operation.model.entity.OperLogEntity;

/**
 * 操作日志  服务类
 *
 * @author bing_huang
 * @since 3.0.0
 */
public interface IOperLogService extends ISuperBaseService<Long, OperLogParams, OperLogDTO, OperLogEntity> {

    /**
     * 清空
     *
     * @return 是否成功
     */
    boolean clean();
}
