package com.feng.boot.admin.project.system.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.feng.boot.admin.project.system.quartz.mapper.IJobLogMapper;
import com.feng.boot.admin.project.system.quartz.model.entity.JobLogEntity;
import com.feng.boot.admin.project.system.quartz.model.query.JobLogParams;
import com.feng.boot.admin.commons.utils.QueryWrapperUtils;
import com.feng.boot.admin.domain.service.impl.SuperBaseServiceImpl;
import com.feng.boot.admin.project.system.quartz.model.dto.JobLogDTO;
import com.feng.boot.admin.project.system.quartz.service.IJobLogService;
import com.feng.boot.admin.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * 任务日志  服务实现类
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Service
public class JobLogServiceImpl extends SuperBaseServiceImpl<Long, JobLogParams, JobLogDTO, JobLogEntity, IJobLogMapper> implements IJobLogService {

    @Override
    public QueryWrapper<JobLogEntity> query(@Nonnull JobLogParams params) {
        QueryWrapper<JobLogEntity> query = QueryWrapperUtils.getQuery(params);
        if (Objects.nonNull(params.getJobId())) {
            query.eq(JobLogEntity.JOB_ID, params.getJobId());
        }
        if (StringUtils.isNotBlank(params.getJobName())) {
            query.eq(JobLogEntity.JOB_NAME, params.getJobName());
        }
        if (StringUtils.isNotBlank(params.getJobGroup())) {
            query.eq(JobLogEntity.JOB_GROUP, params.getJobGroup());
        }
        if (Objects.nonNull(params.getStatus())) {
            query.eq(JobLogEntity.STATUS, params.getStatus());
        }
        if (Objects.nonNull(params.getStartTime())) {
            query.gt(JobLogEntity.START_TIME, params.getStartTime());
        }
        if (Objects.nonNull(params.getEndTime())) {
            query.le(JobLogEntity.END_TIME, params.getEndTime());
        }
        return query;
    }
}
