package com.agileboot.infrastructure.cache.redis;

import com.agileboot.infrastructure.cache.RedisUtil;
import com.agileboot.infrastructure.interceptor.repeatSubmit.RepeatRequest;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author valarchie
 */
@Component
public class RedisCacheService {

    @Autowired
    private RedisUtil redisUtil;

    public RedisCacheTemplate<String> captchaCache;
    public RedisCacheTemplate<LoginUser> loginUserCache;
    public RedisCacheTemplate<RepeatRequest> repeatSubmitCache;

    @PostConstruct
    public void init() {

        captchaCache = new RedisCacheTemplate<>(redisUtil, CacheKeyEnum.CAPTCHAT);

        loginUserCache = new RedisCacheTemplate<LoginUser>(redisUtil, CacheKeyEnum.LOGIN_USER_KEY) {
            @Override
            public LoginUser getCachedObjectById(Object id) {
                return null;
            }
        };

        repeatSubmitCache = new RedisCacheTemplate<>(redisUtil, CacheKeyEnum.REPEAT_SUBMIT_KEY);
    }


}
