package com.ticket.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 黑名单前缀
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * 将 Token 加入黑名单（登出时调用）
     * @param token 要失效的 Token
     * @param expireSeconds Token 剩余有效期（秒），确保黑名单与 Token 同时过期
     */
    public void addToBlacklist(String token, long expireSeconds) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "1", expireSeconds, TimeUnit.SECONDS);
        UserContext.removeUserId();
    }

    //检查 Token 是否在黑名单中
    public boolean isInBlacklist(String token) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public void set(String key, Object value,Integer expireSeconds, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expireSeconds, timeUnit);
    }
    public Long decrBy(String key, Integer count) {
        return redisTemplate.opsForValue().decrement(key, count);
    }
    public Long incrBy(String key, Integer count) {
        return redisTemplate.opsForValue().increment(key, count);
    }
    public Boolean tryLock(String key, Integer expireSeconds, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, "1", expireSeconds, timeUnit);
    }
    public Boolean unlock(String key) {
        return redisTemplate.delete(key);
    }
}