package com.feng.boot.admin.project.system.dict.service;

import com.feng.boot.admin.domain.service.ISuperBaseService;
import com.feng.boot.admin.project.system.dict.model.dto.DictEntryDTO;
import com.feng.boot.admin.project.system.dict.model.entity.DictEntryEntity;
import com.feng.boot.admin.project.system.dict.model.query.DictEntryParams;

/**
 * 数据字典项  服务类
 *
 * @author bing_huang
 * @since 3.0.0
 */
public interface IDictEntryService extends ISuperBaseService<Long, DictEntryParams, DictEntryDTO, DictEntryEntity> {

}
