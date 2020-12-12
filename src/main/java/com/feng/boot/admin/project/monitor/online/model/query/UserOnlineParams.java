package com.feng.boot.admin.project.monitor.online.model.query;

import com.feng.boot.admin.domain.model.query.BaseParams;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 过滤参数
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserOnlineParams extends BaseParams {
    private String ipaddr;
    private String username;
}
