package com.feng.boot.admin.project.system.quartz.service;

import com.feng.boot.admin.project.system.quartz.model.entity.JobLogEntity;
import com.feng.boot.admin.project.system.quartz.model.query.JobLogParams;
import com.feng.boot.admin.domain.service.ISuperBaseService;
import com.feng.boot.admin.project.system.quartz.model.dto.JobLogDTO;

/**
 * 任务日志  服务类
 *
 * @author bing_huang
 * @since 3.0.0
 */
public interface IJobLogService extends ISuperBaseService<Long, JobLogParams, JobLogDTO, JobLogEntity> {

}
