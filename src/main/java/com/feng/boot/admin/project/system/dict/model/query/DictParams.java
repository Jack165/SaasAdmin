package com.feng.boot.admin.project.system.dict.model.query;

import com.feng.boot.admin.domain.model.query.BaseParams;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据字典过滤条件
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class DictParams extends BaseParams {
    private String name;
    private String type;
}
