package com.agileboot.admin.controller.common;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.entity.SysUser;
import com.agileboot.admin.request.LoginDTO;
import com.agileboot.admin.request.RegisterDTO;
import com.agileboot.admin.response.CaptchaDTO;
import com.agileboot.admin.response.UserPermissionDTO;
import com.agileboot.common.config.AgileBootConfig;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.exception.ApiException;
import com.agileboot.common.core.exception.errors.InternalErrorCode;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.domain.system.menu.MenuApplicationService;
import com.agileboot.domain.system.menu.RouterVo;
import com.agileboot.domain.system.user.UserApplicationService;
import com.agileboot.infrastructure.cache.RedisUtil;
import com.agileboot.infrastructure.cache.guava.GuavaCacheService;
import com.agileboot.infrastructure.cache.redis.RedisCacheService;
import com.agileboot.infrastructure.web.service.SysLoginService;
import com.agileboot.infrastructure.web.service.SysPermissionService;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.service.ISysConfigXService;
import com.agileboot.orm.service.ISysUserXService;
import com.google.code.kaptcha.Producer;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 *
 * @author valarchie
 */
@RestController
public class LoginController {

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


    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private MenuApplicationService menuApplicationService;

    @Autowired
    private RedisUtil cache;

    /**
     * 系统基础配置
     */
    @Autowired
    private AgileBootConfig agileBootConfig;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index() {
        return StrUtil.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。",
            agileBootConfig.getName(), agileBootConfig.getVersion());
    }

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

    /**
     * 登录方法
     *
     * @param loginDTO 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public ResponseDTO login(@RequestBody LoginDTO loginDTO) {
        // 生成令牌
        String token = loginService.login(loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.getCode(),
            loginDTO.getUuid());

        return ResponseDTO.ok(MapUtil.of(Constants.TokenConstants.TOKEN, token));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getLoginUserInfo")
    public ResponseDTO getLoginUserInfo() {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(loginUser.getUserId());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(loginUser.getUserId());
        SysUserXEntity byId = userService.getById(loginUser.getUserId());

        UserPermissionDTO permissionDTO = new UserPermissionDTO();
        permissionDTO.setUser(new SysUser(byId));
        permissionDTO.setRoles(roles);
        permissionDTO.setPermissions(permissions);

        return ResponseDTO.ok(permissionDTO);
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    public ResponseDTO getRouters() {
        Long userId = AuthenticationUtils.getUserId();
        List<RouterVo> routerTree = menuApplicationService.getRouterTree(userId);
        return ResponseDTO.ok(routerTree);
    }


    @PostMapping("/register")
    public ResponseDTO register(@RequestBody RegisterDTO user) {
        if (!(Convert.toBool(configService.getConfigValueByKey("sys.account.registerUser")))) {
//            return error("当前系统没有开启注册功能！");
            return ResponseDTO.fail();
        }

//        String msg = userApplicationService.register(user.toModel());
//        return StrUtil.isEmpty("msg") ? success() : error("msg");
        return ResponseDTO.ok();
    }

}
