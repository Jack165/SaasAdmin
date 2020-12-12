package com.feng.boot.admin.configuration;

import com.feng.boot.admin.token.ITokenService;
import com.feng.boot.admin.token.impl.RedisTokenServiceImpl;
import com.feng.boot.admin.configuration.properties.BootAdminProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * boot admin 配置
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Configuration
@RequiredArgsConstructor
public class BooAdminAutoConfiguration {
    private final BootAdminProperties properties;

    /**
     * 配置redis token缓存
     *
     * @return {@link ITokenService}
     */
    @Bean
    public ITokenService tokenService() {
        return new RedisTokenServiceImpl(properties.getTokenConfig());
    }
}
