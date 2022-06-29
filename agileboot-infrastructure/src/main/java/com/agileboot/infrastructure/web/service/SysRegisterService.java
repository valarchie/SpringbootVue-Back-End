package com.agileboot.infrastructure.web.service;

import cn.hutool.core.util.StrUtil;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.constant.UserConstants;
import com.agileboot.common.core.domain.model.RegisterBody;
import com.agileboot.common.exception.user.CaptchaException;
import com.agileboot.common.exception.user.CaptchaExpireException;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.common.utils.MessageUtils;
import com.agileboot.infrastructure.cache.RedisCache;
import com.agileboot.infrastructure.manager.AsyncManager;
import com.agileboot.infrastructure.manager.factory.AsyncFactory;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.service.ISysConfigXService;
import com.agileboot.orm.service.ISysUserXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册校验方法
 *
 * @author ruoyi
 */
@Component
public class SysRegisterService {

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private ISysConfigXService configService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody) {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();

        boolean captchaOnOff = configService.isCaptchaOn();
        // 验证码开关
        if (captchaOnOff) {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StrUtil.isEmpty(username)) {
            msg = "用户名不能为空";
        } else if (StrUtil.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
            || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
            || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (userService.checkUserNameUnique(username)) {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        } else {
            SysUserXEntity entity = new SysUserXEntity();

            entity.setUserName(username);
            entity.setPassword(AuthenticationUtils.encryptPassword(registerBody.getPassword()));

            boolean regFlag = entity.insert();
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLoginInfo(username, Constants.REGISTER,
                    MessageUtils.message("user.register.success")));
            }
        }
        return msg;
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
        String verifyKey = Constants.CAPTCHA_CODE_KEY + StrUtil.emptyIfNull(uuid);
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException();
        }
    }
}
