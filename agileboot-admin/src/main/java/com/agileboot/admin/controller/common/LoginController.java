package com.agileboot.admin.controller.common;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.entity.SysUser;
import com.agileboot.admin.request.LoginDTO;
import com.agileboot.admin.request.RegisterDTO;
import com.agileboot.admin.response.UserPermissionDTO;
import com.agileboot.common.config.AgileBootConfig;
import com.agileboot.common.constant.Constants.Token;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.domain.system.menu.MenuApplicationService;
import com.agileboot.domain.system.menu.RouterVo;
import com.agileboot.domain.system.user.UserApplicationService;
import com.agileboot.infrastructure.cache.RedisUtil;
import com.agileboot.infrastructure.cache.guava.GuavaCacheService;
import com.agileboot.infrastructure.cache.redis.RedisCacheService;
import com.agileboot.infrastructure.web.domain.login.CaptchaDTO;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.service.LoginService;
import com.agileboot.infrastructure.web.util.AuthenticationUtils;
import com.agileboot.orm.service.ISysConfigXService;
import com.agileboot.orm.service.ISysUserXService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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
    private LoginService loginService;

    @Autowired
    private ISysUserXService userService;

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
    public ResponseDTO<CaptchaDTO> getCaptchaImg() {
        CaptchaDTO captchaImg = loginService.getCaptchaImg();
        return ResponseDTO.ok(captchaImg);
    }

    /**
     * 登录方法
     *
     * @param loginDTO 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public ResponseDTO<Map> login(@RequestBody LoginDTO loginDTO) {
        // 生成令牌
        String token = loginService.login(loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.getCode(),
            loginDTO.getUuid());

        return ResponseDTO.ok(MapUtil.of(Token.TOKEN_FIELD, token));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getLoginUserInfo")
    public ResponseDTO getLoginUserInfo() {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        UserPermissionDTO permissionDTO = new UserPermissionDTO();
        permissionDTO.setUser(new SysUser(loginUser.getEntity()));
        permissionDTO.setRoles(loginUser.getRoleKeys());
        permissionDTO.setPermissions(loginUser.getMenuPermissions());

        return ResponseDTO.ok(permissionDTO);
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    public ResponseDTO<List<RouterVo>> getRouters() {
        Long userId = AuthenticationUtils.getUserId();
        List<RouterVo> routerTree = menuApplicationService.getRouterTree(userId);
        return ResponseDTO.ok(routerTree);
    }


    @PostMapping("/register")
    public ResponseDTO register(@RequestBody RegisterDTO user) {
        loginService.register(null);
        return ResponseDTO.ok();
    }

}
