package com.feng.boot.admin.commons.http.inter;

import com.feng.boot.admin.commons.http.HttpHeader;
import com.feng.boot.admin.commons.http.config.HttpConfig;
import lombok.Getter;

/**
 * 异步 请求 抽象
 *
 * @author bing_huang
 * @since 1.0.1
 */
public abstract class AbstractAsyncHttp implements AsyncHttp {
    protected HttpConfig httpConfig;
    @Getter
    protected HttpHeader header;

    public HttpConfig getHttpConfig() {
        return httpConfig;
    }

    public AbstractAsyncHttp setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig == null ? HttpConfig.builder().build() : httpConfig;
        return this;
    }

    public AbstractAsyncHttp setHeader(HttpHeader header) {
        this.header = header;
        return this;
    }

    public AbstractAsyncHttp(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }
}
