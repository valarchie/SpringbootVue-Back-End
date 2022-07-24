package com.agileboot.admin.controller.common;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.IdUtil;
import com.agileboot.admin.response.CaptchaDTO;
import com.agileboot.common.config.AgileBootConfig;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.exception.ApiException;
import com.agileboot.common.core.exception.errors.InternalErrorCode;
import com.agileboot.domain.system.user.UserApplicationService;
import com.agileboot.infrastructure.cache.RedisUtil;
import com.agileboot.infrastructure.cache.guava.GuavaCacheService;
import com.agileboot.infrastructure.cache.redis.RedisCacheService;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.service.ISysConfigXService;
import com.google.code.kaptcha.Producer;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verification Code Endpoint
 *
 * @author valarchie
 */
@RestController
public class CaptchaController {

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private GuavaCacheService guavaCacheService;

    @Autowired
    private ISysConfigXService configService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private UserApplicationService userApplicationService;


    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public ResponseDTO<CaptchaDTO> getCode(HttpServletResponse response) throws ExecutionException {
        CaptchaDTO captchaDTO = new CaptchaDTO();

        List<SysUserXEntity> userList = new ArrayList<>();
        SysUserXEntity entity = new SysUserXEntity();
        entity.setUserId(1L);
        entity.setUsername("hajj ");
        userList.add(entity);

        String configValue = guavaCacheService.configCache.get("sys.account.captchaOnOff").orElseThrow(
            () -> new ApiException(InternalErrorCode.INVALID_PARAMETER));

        boolean captchaOnOff = Convert.toBool(configValue);;
        captchaDTO.setCaptchaOnOff(captchaOnOff);
        if (!captchaOnOff) {
            return ResponseDTO.ok(captchaDTO);
        }

        String capStr, code = null;
        BufferedImage image = null;

        // 生成验证码
        String captchaType = AgileBootConfig.getCaptchaType();
        if ("math".equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        if (image == null) {
            return ResponseDTO.fail(InternalErrorCode.LOGIN_CAPTCHA_GENERATE_FAIL);
        }

        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();

        redisCacheService.captchaCache.set(uuid, code);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        ImgUtil.writeJpg(image, os);

        captchaDTO.setUuid(uuid);
        captchaDTO.setImg(Base64.encode(os.toByteArray()));
        return ResponseDTO.ok(captchaDTO);
    }
}
