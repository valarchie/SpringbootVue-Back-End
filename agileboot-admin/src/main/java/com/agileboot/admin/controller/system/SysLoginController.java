package com.agileboot.admin.controller.system;

import com.agileboot.admin.deprecated.entity.SysUser;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.core.domain.Rdto;
import com.agileboot.common.core.domain.model.LoginBody;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.domain.system.menu.MenuApplicationService;
import com.agileboot.infrastructure.web.service.SysLoginService;
import com.agileboot.infrastructure.web.service.SysPermissionService;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.service.ISysUserXService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@RestController
public class SysLoginController {

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private MenuApplicationService menuApplicationService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public Rdto login(@RequestBody LoginBody loginBody) {
        Rdto ajax = Rdto.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
            loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public Rdto getInfo() {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(loginUser.getUserId());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(loginUser.getUserId());
        Rdto ajax = Rdto.success();
        SysUserXEntity byId = userService.getById(loginUser.getUserId());

        ajax.put("user", new SysUser(byId));
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public Rdto getRouters() {
        Long userId = AuthenticationUtils.getUserId();
        return Rdto.success(menuApplicationService.getRouterTree(userId));
    }
}
