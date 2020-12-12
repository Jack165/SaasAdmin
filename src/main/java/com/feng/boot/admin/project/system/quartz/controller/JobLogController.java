package com.feng.boot.admin.project.system.quartz.controller;


import com.feng.boot.admin.project.system.quartz.service.IJobLogService;
import com.feng.boot.admin.annotation.ClassDescribe;
import com.feng.boot.admin.annotation.PreAuth;
import com.feng.boot.admin.domain.controller.SuperSimpleBaseController;
import com.feng.boot.admin.project.system.quartz.model.dto.JobLogDTO;
import com.feng.boot.admin.project.system.quartz.model.entity.JobLogEntity;
import com.feng.boot.admin.project.system.quartz.model.query.JobLogParams;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务日志  前端控制器
 *
 * @author bing_huang
 * @since 3.0.0
 */
@RestController
@RequestMapping("/api/v3/system/job/log")
@ClassDescribe("任务日志")
@PreAuth("job:log")
public class JobLogController extends SuperSimpleBaseController<Long, JobLogDTO, JobLogParams, JobLogEntity> {
    private final IJobLogService service;

    public JobLogController(IJobLogService service) {
        super(service);
        this.service = service;
    }
}

