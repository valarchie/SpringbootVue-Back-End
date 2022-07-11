package com.agileboot.infrastructure.cache.redis;

import com.agileboot.infrastructure.cache.RedisUtil;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author valarchie
 */
@Component
public class RedisCacheManager {

    @Autowired
    private RedisUtil redisUtil;

    public RedisCacheTemplate<String> captchaCache;

    @PostConstruct
    public void init() {
        captchaCache = new RedisCacheTemplate<>(redisUtil, RedisCacheEnum.CAPTCHAT);
    }


}
