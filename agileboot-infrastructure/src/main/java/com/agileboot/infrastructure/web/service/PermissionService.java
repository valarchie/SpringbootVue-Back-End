package com.agileboot.infrastructure.web.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.domain.login.Role;
import com.agileboot.infrastructure.web.util.AuthenticationUtils;
import com.agileboot.orm.entity.SysUserEntity;
import com.agileboot.orm.enums.DataScopeEnum;
import com.agileboot.orm.service.ISysDeptService;
import com.agileboot.orm.service.ISysUserService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * RuoYi首创 自定义权限实现，ss取自SpringSecurity首字母
 *
 * @author ruoyi valarchie
 */
@Service("ss")
public class PermissionService {

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysUserService userService;

    /**
     * 所有权限标识
     */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 管理员角色权限标识
     */
    private static final String SUPER_ADMIN = "admin";

    private static final String ROLE_DELIMITER = ",";

    private static final String PERMISSION_DELIMITER = ",";

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
     * 通过userId 校验当前用户 对 目标用户是否有操作权限
     * @param userId
     * @return
     */
    public boolean checkDataScopeWithUserId(Long userId) {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        if (loginUser == null) {
            return false;
        }
        Role role = loginUser.getRole();

        SysUserEntity targetUser = userService.getById(userId);

        if(role.getDataScope() == DataScopeEnum.ALL.getValue()) {
            return true;
        }

        if (role.getDataScope() == DataScopeEnum.SELF_DEFINE.getValue() &&
            CollUtil.safeContains(role.getDeptIdSet(), targetUser.getDeptId())) {
            return true;
        }

        if (role.getDataScope() == DataScopeEnum.CURRENT_DEPT_AND_CHILDREN_DEPT.getValue() &&
            deptService.isChildOfTargetDeptId(loginUser.getDeptId(), targetUser.getDeptId())) {
            return true;
        }

        return false;
    }

    public boolean checkDataScopeWithDeptId(Long deptId) {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        if (loginUser == null) {
            return false;
        }
        Role role = loginUser.getRole();

        if(role.getDataScope() == DataScopeEnum.ALL.getValue()) {
            return true;
        }

        if (role.getDataScope() == DataScopeEnum.SELF_DEFINE.getValue() &&
            CollUtil.safeContains(role.getDeptIdSet(), deptId)) {
            return true;
        }

        if (role.getDataScope() == DataScopeEnum.CURRENT_DEPT_AND_CHILDREN_DEPT.getValue() &&
            deptService.isChildOfTargetDeptId(loginUser.getDeptId(), deptId)) {
            return true;
        }

        return false;
    }


    /**
     * 验证用户是否不具备某权限，与 hasPermi逻辑相反
     *
     * @param permission 权限字符串
     * @return 用户是否不具备某权限
     */
    public boolean lacksPerm(String permission) {
        return !hasPermi(permission);
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
        for (String permission : permissions.split(PERMISSION_DELIMITER)) {
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
     * @param targetRoleKeys 以 ROLE_NAMES_DELIMETER 为分隔符的角色列表
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
        for (String targetKey : targetRoleKeys.split(ROLE_DELIMITER)) {
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
