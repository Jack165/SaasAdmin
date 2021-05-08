package com.feng.boot.admin.commons.utils;

import com.feng.boot.admin.commons.constant.RedisConstant;
import com.feng.boot.admin.commons.cache.Cache;
import com.feng.boot.admin.commons.cache.impl.remote.RedisSpringDataCache;
import com.feng.boot.admin.commons.spring.SpringContextUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;

/**
 * option cache
 *
 * @author bing_huang
 * @since 3.0.0
 */
public class OptionCacheUtils {
    /**
     * 获取缓存
     *
     * @param key key 不为空
     * @return value
     */
    public static Optional<Map<String, Object>> getCacheValue(@NonNull String key) {
        Assert.hasText(key, "key can not be blank");
        @SuppressWarnings("unchecked")
        Cache<String, Map<String, Object>> cache = SpringContextUtils.getBean(RedisSpringDataCache.class);
        try {
            return cache.get(RedisConstant.OPTIONS_KEY_PREFIX + key);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 删除key
     *
     * @param key key 不为空
     */
    public static void deleteCache(@NonNull String key) {
        Assert.hasText(key, "key can not be blank");
        @SuppressWarnings("unchecked")
        Cache<String, Map<String, Object>> cache = SpringContextUtils.getBean(RedisSpringDataCache.class);
        cache.delete(RedisConstant.OPTIONS_KEY_PREFIX + key);
    }

    /**
     * 设置缓存
     *
     * @param key    key
     * @param values value
     */
    public static void setCache(@NonNull String key, Map<String, Object> values) {
        Assert.hasText(key, "key can not be blank");
        @SuppressWarnings("unchecked")
        Cache<String, Map<String, Object>> cache = SpringContextUtils.getBean(RedisSpringDataCache.class);
        cache.put(RedisConstant.OPTIONS_KEY_PREFIX + key, values);
    }
}
