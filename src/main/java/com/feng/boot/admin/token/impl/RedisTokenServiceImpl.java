package com.feng.boot.admin.token.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feng.boot.admin.security.model.User;
import com.feng.boot.admin.token.AbstractTokenService;
import com.google.common.collect.Maps;
import com.feng.boot.admin.token.configuration.TokenProperties;
import com.feng.commons.cache.Cache;
import com.feng.commons.json.exceptions.JsonException;
import com.feng.commons.json.utils.Jsons;
import com.feng.commons.lang.StringUtils;
import com.feng.commons.lang.collection.CollectionUtils;
import com.feng.commons.lang.date.DateUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 实现
 *
 * @author bing_huang
 * @since 3.0.0
 */
public class RedisTokenServiceImpl extends AbstractTokenService {
    @Resource(name = "redisCache")
    private Cache<String, Object> cache;
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private ObjectMapper jacksonObjectMapper;

    public RedisTokenServiceImpl(TokenProperties properties) {
        super(properties);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        String accessToken = getAccessToken(request);
        if (StringUtils.isNotBlank(accessToken)) {
            String accessTokenKey = getAccessTokenKey(accessToken);
            String userTokenKey = String.valueOf(cache.get(accessTokenKey).orElseGet(() -> ""));
            Optional<Object> optional = cache.get(getUserTokenKey(userTokenKey));
            if (optional.isPresent()) {
                try {
                    return Jsons.JSONS.jsonToObject(Jsons.JSONS.objectToJson(optional.get()), User.class, jacksonObjectMapper);
                } catch (JsonException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public String createAccessToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setToken(token);

        setUserAgent(user);
        refreshAccessToken(user);
        String accessToken = extractKey(token);
        String accessTokenKey = getAccessTokenKey(accessToken);
        cache.put(accessTokenKey, token, super.getProperties().getExpireTime(), super.getProperties().getTimeUnit());
        return accessToken;
    }

    @Override
    public void refreshAccessToken(User user) {
        // 获取过期时长
        long expire = TimeUnit.MILLISECONDS.convert(super.getProperties().getExpireTime(), super.getProperties().getTimeUnit());
        // 设置登录时长与过期时间
        user.setLoginTime(DateUtils.now());
        Date expireTim = DateUtils.add(user.getLoginTime(), expire, TimeUnit.MILLISECONDS);
        user.setExpireTime(expireTim);
        //缓存用户
        String userTokenKey = getUserTokenKey(user.getToken());
        cache.put(userTokenKey, user, expire, TimeUnit.MILLISECONDS);
    }

    @Override
    public void verifyAccessToken(HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (Objects.nonNull(loginUser)) {
            Date expireTime = loginUser.getExpireTime();
            Date currentTime = DateUtils.now();
            long refreshTime = TimeUnit.MILLISECONDS.convert(super.getProperties().getRefreshTime(), super.getProperties().getTimeUnit());
            // 校验 刷新
            if (expireTime.getTime() - currentTime.getTime() <= refreshTime) {
                String accessToken = getAccessToken(request);
                String accessTokenKey = getAccessTokenKey(accessToken);
                cache.put(accessTokenKey, loginUser.getToken(), super.getProperties().getExpireTime(), super.getProperties().getTimeUnit());
                refreshAccessToken(loginUser);
            }
        }
    }

    @Override
    public void delLoginUser(HttpServletRequest request) {
        String accessToken = getAccessToken(request);
        if (StringUtils.isNotBlank(accessToken)) {
            deleteAccessToken(accessToken);
        }

    }

    @Override
    public void deleteAccessToken(String accessToken) {
        if (StringUtils.isNotBlank(accessToken)) {
            String token = String.valueOf(cache.get(getAccessTokenKey(accessToken)).orElseGet(() -> ""));
            cache.delete(getUserTokenKey(token));
            cache.delete(getAccessTokenKey(accessToken));
        }
    }

    @Override
    public Map<String, UserDetails> getOnline() {
        Map<String, UserDetails> maps = Maps.newHashMap();
        try {

            Set<String> keys = redisTemplate.keys(getAccessTokenKey("*"));
            if (CollectionUtils.isEmpty(keys)) {
                return maps;
            }
            for (String key : keys) {
                String accessToken = org.apache.commons.lang3.StringUtils.remove(key, LOGIN_TOKEN_KEY_PREFIX);
                Optional<Object> optionalKey = cache.get(getAccessTokenKey(accessToken));
                if (optionalKey.isPresent()) {
                    String tokenKey = (String) optionalKey.get();
                    Optional<Object> optional = cache.get(getUserTokenKey(tokenKey));
                    if (optional.isPresent()) {
                        User cacheUser = Jsons.JSONS.jsonToObject(Jsons.JSONS.objectToJson(optional.get()), User.class, jacksonObjectMapper);
                        maps.put(accessToken, cacheUser);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }
}
