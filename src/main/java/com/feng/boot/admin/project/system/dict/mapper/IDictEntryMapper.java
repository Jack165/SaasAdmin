package com.feng.boot.admin.project.system.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.boot.admin.project.system.dict.model.entity.DictEntryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典项  Mapper 接口
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Mapper
public interface IDictEntryMapper extends BaseMapper<DictEntryEntity> {

}
