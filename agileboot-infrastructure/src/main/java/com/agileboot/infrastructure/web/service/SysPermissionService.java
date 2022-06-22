package com.agileboot.infrastructure.web.service;

import com.agileboot.orm.service.ISysUserXService;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户权限处理
 *
 * @author ruoyi
 */
@Component
public class SysPermissionService {

    @Autowired
    private ISysUserXService userService;


    /**
     * 获取角色数据权限
     *
     * @param userId 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(Long userId) {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (isAdmin(userId)) {
            roles.add("admin");
        } else {
            roles.addAll(userService.selectRolePermissionByUserId(userId));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     *
     * @param userId 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(Long userId) {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (isAdmin(userId)) {
            perms.add("*:*:*");
        } else {
            perms.addAll(userService.selectMenuPermsByUserId(userId));
        }
        return perms;
    }


    public boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

}
