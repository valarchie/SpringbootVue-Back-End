package com.agileboot.web.controller.system;

import com.agileboot.common.constant.Constants;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.domain.model.LoginBody;
import com.agileboot.infrastructure.loginuser.AuthenticationUtils;
import com.agileboot.infrastructure.web.service.SysLoginService;
import com.agileboot.infrastructure.web.service.SysPermissionService;
import com.springvue.orm.domain.entity.SysUser;
import com.springvue.orm.service.ISysMenuService;
import java.util.List;
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
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public ResponseDTO login(@RequestBody LoginBody loginBody) {
        ResponseDTO ajax = ResponseDTO.success();
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
    public ResponseDTO getInfo() {
        SysUser user = AuthenticationUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        ResponseDTO ajax = ResponseDTO.success();
        ajax.put("user", user);
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
    public ResponseDTO getRouters() {
        Long userId = AuthenticationUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return ResponseDTO.success(menuService.buildMenus(menus));
    }
}
