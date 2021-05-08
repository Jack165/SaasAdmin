package com.feng.boot.admin.commons.cache.config;

import com.feng.boot.admin.commons.cache.support.serial.impl.JdkCacheSerializer;
import com.feng.boot.admin.commons.cache.support.serial.Serializer;
import lombok.Data;

import java.util.function.Function;

/**
 * cache configuration
 *
 * @author bing_huang
 * @since 1.0.0
 */
@Data
public class CacheConfig<K, V> {
    /**
     * key 转换器
     */
    private Function<K, V> keyConverter;
    /**
     * 序列化
     */
    private Serializer serializer = JdkCacheSerializer.INSTANCE;
}
