package com.feng.boot.admin.commons.http.inter;

import com.feng.boot.admin.commons.http.HttpHeader;

/**
 * @author bing_huang
 * @since 1.0.1
 */
public interface Http {
    String HTTP = "http";
    String HTTPS = "https";
    HttpHeader DEFAULT_HEADER = HttpHeader.builder();
}
