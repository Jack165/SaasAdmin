package com.feng.boot.admin.project.system.post.service;

import com.feng.boot.admin.domain.service.ISuperBaseService;
import com.feng.boot.admin.domain.service.base.ISuperPoiService;
import com.feng.boot.admin.project.system.post.model.dto.PostDTO;
import com.feng.boot.admin.project.system.post.model.dto.PostExcelDTO;
import com.feng.boot.admin.project.system.post.model.entity.PostEntity;
import com.feng.boot.admin.project.system.post.model.query.PostParams;

/**
 * 岗位  服务类
 *
 * @author bing_huang
 * @since 3.0.0
 */
public interface IPostService extends ISuperBaseService<Long, PostParams, PostDTO, PostEntity>, ISuperPoiService<PostParams, PostExcelDTO> {

}
