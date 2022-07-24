package com.agileboot.infrastructure.web.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.core.exception.ApiException;
import com.agileboot.common.core.exception.errors.BusinessErrorCode;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.common.utils.ServletHolderUtil;
import com.agileboot.common.utils.i18n.MessageUtils;
import com.agileboot.infrastructure.cache.RedisUtil;
import com.agileboot.infrastructure.cache.redis.RedisCacheService;
import com.agileboot.infrastructure.thread.AsyncTaskFactory;
import com.agileboot.infrastructure.thread.ThreadPoolManager;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.service.ISysConfigXService;
import com.agileboot.orm.service.ISysUserXService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Component
@Slf4j
public class SysLoginService {

    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private ISysConfigXService configService;

    @Autowired
    private RedisCacheService redisCacheService;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) {
        boolean captchaOnOff = configService.isCaptchaOn();
        // 验证码开关
        if (captchaOnOff) {
            validateCaptcha(username, code, uuid);
        }
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {

                ThreadPoolManager.execute(AsyncTaskFactory.recordLoginInfo(username, Constants.LOGIN_FAIL,
                    MessageUtils.message("user.password.not.match")));
                throw new ApiException(BusinessErrorCode.LOGIN_WRONG_USER_PASSWORD);
            } else {
                ThreadPoolManager.execute(AsyncTaskFactory.recordLoginInfo(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ApiException(e.getCause(), BusinessErrorCode.LOGIN_ERROR, e.getMessage());
            }
        }
        ThreadPoolManager.execute(AsyncTaskFactory.recordLoginInfo(username, Constants.LOGIN_SUCCESS,
            MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {

//        String verifyKey = Constants.CAPTCHA_CODE_KEY + StrUtil.emptyIfNull(uuid);

//        String captcha = redisUtil.getCacheObject(verifyKey);
        String captcha = redisCacheService.captchaCache.getById(uuid);
        redisCacheService.captchaCache.delete(uuid);

//        redisUtil.deleteObject(verifyKey);
        if (captcha == null) {
            ApiException apiException = new ApiException(BusinessErrorCode.CAPTCHA_CODE_EXPIRE);
            ThreadPoolManager.execute(AsyncTaskFactory.recordLoginInfo(username, Constants.LOGIN_FAIL,
                apiException.getLocalizedMessage()));
            throw apiException;
        }
        if (!code.equalsIgnoreCase(captcha)) {
            ApiException apiException = new ApiException(BusinessErrorCode.CAPTCHA_CODE_WRONG);
            ThreadPoolManager.execute(AsyncTaskFactory.recordLoginInfo(username, Constants.LOGIN_FAIL,
                apiException.getLocalizedMessage()));
            throw apiException;
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUserXEntity entity = new SysUserXEntity();

        entity.setUserId(userId);
        entity.setLoginIp(ServletUtil.getClientIP(ServletHolderUtil.getRequest()));
        entity.setLoginDate(DateUtil.date());
        entity.updateById();
    }
}
