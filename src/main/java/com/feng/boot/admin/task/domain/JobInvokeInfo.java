package com.feng.boot.admin.task.domain;

import com.feng.boot.admin.task.utils.JobInvokeUtil;
import lombok.*;

/**
 * job invoke info
 *
 * @author bing_huang
 * @see JobInvokeUtil
 * @since 3.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JobInvokeInfo {
    /**
     * bean
     */
    private String bean;
    /**
     * method
     */
    private String method;
    /**
     * method params
     */
    private String params;

}
