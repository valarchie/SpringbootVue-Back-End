package com.agileboot.infrastructure.cache.redis;

import com.agileboot.infrastructure.cache.RedisUtil;

/**
 * 缓存接口实现类
 */
public class RedisCacheTemplate<T> {

    public RedisUtil redisUtil;
    public RedisCacheEnum redisRedisEnum;

    public RedisCacheTemplate(RedisUtil redisUtil, RedisCacheEnum redisRedisEnum) {
        this.redisUtil = redisUtil;
        this.redisRedisEnum = redisRedisEnum;
    }

    public T getById(Object id) {
        return redisUtil.getCacheObject(generateKey(id));
    }

    public void set(Object id, T obj) {
        redisUtil.setCacheObject(generateKey(id), obj, redisRedisEnum.expiration(), redisRedisEnum.timeUnit());
    }

    public void delete(Object id) {
        redisUtil.deleteObject(generateKey(id));
    }

    public void refresh(Object id) {
        redisUtil.expire(generateKey(id), redisRedisEnum.expiration(), redisRedisEnum.timeUnit());
    }

    public String generateKey(Object id) {
        return redisRedisEnum.key() + id;
    }

}
