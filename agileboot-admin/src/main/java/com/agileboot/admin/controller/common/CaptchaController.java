package com.agileboot.admin.controller.common;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.IdUtil;
import com.agileboot.admin.request.CaptchaDTO;
import com.agileboot.common.config.AgileBootConfig;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.infrastructure.cache.RedisCache;
import com.agileboot.orm.service.ISysConfigXService;
import com.google.code.kaptcha.Producer;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
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
    private RedisCache redisCache;

    @Autowired
    private ISysConfigXService configService;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public ResponseDTO<CaptchaDTO> getCode(HttpServletResponse response) {
        CaptchaDTO captchaDTO = new CaptchaDTO();

        boolean captchaOnOff = configService.isCaptchaOn();
        captchaDTO.setCaptchaOnOff(captchaOnOff);
        if (!captchaOnOff) {
            return ResponseDTO.ok(captchaDTO);
        }

        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

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
//            return ResponseDTO.fail("验证码生成失败");
        }

        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出

        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        ImgUtil.writeJpg(image, os);

        captchaDTO.setUuid(uuid);
        captchaDTO.setImg(Base64.encode(os.toByteArray()));
        return ResponseDTO.ok(captchaDTO);
    }
}
