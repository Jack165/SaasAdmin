package com.feng.boot.admin.project.system.post.model.query;

import com.feng.boot.admin.domain.model.query.BaseParams;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 岗位检索
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class PostParams extends BaseParams {
    private String name;
    private String number;
    private Integer isEnabled;
}
