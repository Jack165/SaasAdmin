package com.feng.boot.admin.commons.cache.impl;

import com.feng.boot.admin.commons.cache.Cache;
import com.feng.boot.admin.commons.cache.CacheWrapper;
import com.feng.boot.admin.commons.lang.date.DateMsUnit;
import com.feng.boot.admin.commons.lang.collection.CollectionUtils;
import com.feng.boot.admin.commons.lang.date.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存抽象
 *
 * @author bing_huang
 * @since 1.0.0
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCache.class);

    /**
     * 根据key获取缓存包装
     *
     * @param key 缓存key 不为null
     * @return cache wrapper
     */
    @Nonnull
    protected abstract Optional<CacheWrapper<V>> getInternal(@Nonnull K key);

    /**
     * 根据keys获取包装集合
     *
     * @param keys keys
     * @return 缓存值
     * @since 2.0.0
     */
    @Nonnull
    protected abstract Optional<Map<K, CacheWrapper<V>>> getInternal(@Nonnull Set<K> keys);

    /**
     * 设置缓存
     *
     * @param key          key不为null
     * @param cacheWrapper cache wrapper 不为null
     */
    protected abstract void putInternal(@Nonnull K key, @Nonnull CacheWrapper<V> cacheWrapper);

    /**
     * 设置缓存 , key不存在则设置时效缓存，否则不操作
     *
     * @param key          缓存key,不为null
     * @param cacheWrapper cache wrapper,不为null
     * @return true:缓存key不存在并已重新设置;false:缓存前key存在;null:其他原因
     */
    protected abstract Boolean putInternalIfAbsent(@Nonnull K key, @Nonnull CacheWrapper<V> cacheWrapper);


    @Nonnull
    @Override
    public Optional<V> get(@Nonnull K key) {
        Assert.notNull(key, "Cache key must not be null");
        return getInternal(key).map(wrapper -> {
            // Check expiration
            if (wrapper.getExpireAt() != null && wrapper.getExpireAt().before(DateUtils.now())) {
                // Expired then delete it
                LOGGER.warn("Cache key: [{}] has been expired", key);

                // Delete the key
                delete(key);

                // Return null
                return null;
            }
            return wrapper.getData();
        });
    }

    @Override
    public Optional<List<V>> get(@Nonnull Set<K> keys) {
        Assert.notEmpty(keys, "Cache key must not be null");
        List<K> keyList = new ArrayList<>(keys.size());
        Optional<List<V>> result = getInternal(keys).map(map -> {
            return map.entrySet().stream().map(entry -> {
                CacheWrapper<V> wrapper = entry.getValue();
                // Check expiration
                if (wrapper.getExpireAt() != null && wrapper.getExpireAt().before(DateUtils.now())) {
                    // Expired then delete it
                    LOGGER.warn("Cache key: [{}] has been expired", entry.getKey());
                    // Delete the key
                    keyList.add(entry.getKey());

                    // Return null
                    return null;
                }
                return wrapper.getData();
            }).collect(Collectors.toList());
        });
        if (!CollectionUtils.isEmpty(keyList)) {
            delete(CollectionUtils.newHashSet(keyList));
        }
        return result;

    }

    @Override
    public void put(@Nonnull K key, @Nonnull V value, long timeout, @Nonnull TimeUnit timeUnit) {
        this.putInternal(key, this.buildCacheWrapper(value, timeout, timeUnit));
    }

    @Override
    public Boolean putIfAbsent(@Nonnull K key, @Nonnull V value, long timeout, @Nonnull TimeUnit timeUnit) {
        return this.putInternalIfAbsent(key, this.buildCacheWrapper(value, timeout, timeUnit));
    }

    @Override
    public void put(@Nonnull K key, @Nonnull V value) {
        this.putInternal(key, this.buildCacheWrapper(value, -1L, null));
    }

    /**
     * 构建cache wrapper
     *
     * @param value    cache value
     * @param timeout  cache time out  不能小于1
     * @param timeUnit Time out type
     * @return cache wrapper
     */
    private CacheWrapper<V> buildCacheWrapper(@Nonnull V value, Long timeout, @Nullable TimeUnit timeUnit) {
        Assert.notNull(value, "cache value must not be null");
        Assert.isTrue(timeout >= -1, "cache expiration timout must not be less than 1");
        Date now = DateUtils.now();
        Date expireAt = null;
        if (timeout > 0 && timeUnit != null) {
            expireAt = DateUtils.add(now, timeout, timeUnit);
        } else if (timeout <= 0) {
            expireAt = DateUtils.addYears(now, 999);
        }

        // Build cache wrapper
        CacheWrapper<V> cacheWrapper = new CacheWrapper<>();
        cacheWrapper.setCreateAt(now);
        cacheWrapper.setExpireAt(expireAt);
        cacheWrapper.setData(value);

        return cacheWrapper;
    }

    protected long expireTimeMs(Date createAt, Date expireAt) {
        return DateUtils.between(createAt, expireAt, DateMsUnit.MS);
    }
}
