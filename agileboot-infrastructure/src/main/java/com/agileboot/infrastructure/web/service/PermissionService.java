package com.agileboot.infrastructure.web.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.common.loginuser.LoginUser;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * RuoYi首创 自定义权限实现，ss取自SpringSecurity首字母
 *
 * @author ruoyi
 */
@Service("ss")
public class PermissionService {

    /**
     * 所有权限标识
     */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 管理员角色权限标识
     */
    private static final String SUPER_ADMIN = "admin";

    private static final String ROLE_DELIMETER = ",";

    private static final String PERMISSION_DELIMETER = ",";

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermi(String permission) {
        if (StrUtil.isEmpty(permission)) {
            return false;
        }
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        if (loginUser == null || CollectionUtils.isEmpty(loginUser.getMenuPermissions())) {
            return false;
        }
        return hasPermissions(loginUser.getMenuPermissions(), permission);
    }

    /**
     * 验证用户是否不具备某权限，与 hasPermi逻辑相反
     *
     * @param permission 权限字符串
     * @return 用户是否不具备某权限
     */
    public boolean lacksPerm(String permission) {
        return hasPermi(permission) != true;
    }

    /**
     * 验证用户是否具有以下任意一个权限
     *
     * @param permissions 以 PERMISSION_NAMES_DELIMETER 为分隔符的权限列表
     * @return 用户是否具有以下任意一个权限
     */
    public boolean hasAnyPermi(String permissions) {
        if (StrUtil.isEmpty(permissions)) {
            return false;
        }
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        if (loginUser == null || CollectionUtils.isEmpty(loginUser.getMenuPermissions())) {
            return false;
        }
        Set<String> authorities = loginUser.getMenuPermissions();
        for (String permission : permissions.split(PERMISSION_DELIMETER)) {
            if (permission != null && hasPermissions(authorities, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否拥有某个角色
     *
     * @param roleKey 角色字符串
     * @return 用户是否具备某角色
     */
    public boolean hasRole(String roleKey) {
        if (StrUtil.isEmpty(roleKey)) {
            return false;
        }
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        if (loginUser == null || CollUtil.isEmpty(loginUser.getRoleKeys())) {
            return false;
        }

        return loginUser.getRoleKeys().contains(roleKey) || loginUser.getRoleKeys().contains(SUPER_ADMIN);

    }

    /**
     * 验证用户是否不具备某角色，与 isRole逻辑相反。
     *
     * @param role 角色名称
     * @return 用户是否不具备某角色
     */
    public boolean lacksRole(String role) {
        return !hasRole(role);
    }

    /**
     * 验证用户是否具有以下任意一个角色
     *
     * @param roles 以 ROLE_NAMES_DELIMETER 为分隔符的角色列表
     * @return 用户是否具有以下任意一个角色
     */
    public boolean hasAnyRoles(String targetRoleKeys) {
        if (StrUtil.isEmpty(targetRoleKeys)) {
            return false;
        }
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        if (loginUser == null || CollectionUtils.isEmpty(loginUser.getRoleKeys())) {
            return false;
        }
        Set<String> roleKeys = loginUser.getRoleKeys();
        for (String targetKey : targetRoleKeys.split(ROLE_DELIMETER)) {
            if (roleKeys.contains(targetKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否包含权限
     *
     * @param permissions 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(Set<String> permissions, String permission) {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(StrUtil.trim(permission));
    }
}
