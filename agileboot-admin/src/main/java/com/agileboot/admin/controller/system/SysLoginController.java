package com.agileboot.admin.controller.system;

import cn.hutool.core.map.MapUtil;
import com.agileboot.admin.deprecated.entity.SysUser;
import com.agileboot.admin.request.LoginDTO;
import com.agileboot.admin.response.UserPermissionDTO;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.domain.system.menu.MenuApplicationService;
import com.agileboot.domain.system.menu.RouterVo;
import com.agileboot.infrastructure.cache.RedisUtil;
import com.agileboot.infrastructure.web.service.SysLoginService;
import com.agileboot.infrastructure.web.service.SysPermissionService;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.service.ISysUserXService;
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
    private ISysUserXService userService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private MenuApplicationService menuApplicationService;

    @Autowired
    private RedisUtil cache;

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
    @GetMapping("getInfo")
    public ResponseDTO getInfo() {
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
    @GetMapping("getRouters")
    public ResponseDTO getRouters() {
        Long userId = AuthenticationUtils.getUserId();
        List<RouterVo> routerTree = menuApplicationService.getRouterTree(userId);
        return ResponseDTO.ok(routerTree);
    }
}
