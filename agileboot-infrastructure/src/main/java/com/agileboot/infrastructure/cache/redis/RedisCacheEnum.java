package com.agileboot.infrastructure.cache.redis;

import java.util.concurrent.TimeUnit;

/**
 * @author valarchie
 */
public enum RedisCacheEnum {

    /**
     * Redis各类缓存集合
     */
    CAPTCHAT("captcha_codes:", 2, TimeUnit.MINUTES);


    RedisCacheEnum(String key, int expiration, TimeUnit timeUnit) {
        this.key = key;
        this.expiration = expiration;
        this.timeUnit = timeUnit;
    }

    private final String key;
    private final int expiration;
    private final TimeUnit timeUnit;

    public String key() {
        return key;
    }

    public int expiration() {
        return expiration;
    }

    public TimeUnit timeUnit() {
        return timeUnit;
    }

}
