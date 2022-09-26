package com.agileboot.infrastructure.web.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.domain.login.Role;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.enums.UserStatusEnum;
import com.agileboot.orm.service.ISysRoleXService;
import com.agileboot.orm.service.ISysUserXService;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户验证处理
 *
 * @author ruoyi valarchie
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private ISysRoleXService roleService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserXEntity user = userService.getUserByUserName(username);
        if (user == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new ApiException(BusinessErrorCode.USER_NON_EXIST, username);
        }
        if (!Objects.equals(UserStatusEnum.NORMAL.getValue(), user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ApiException(BusinessErrorCode.USER_IS_DISABLE, username);
        }
        Set<String> roleKeys = getRoleKeys(user.getUserId());
        Set<String> menuPermissions = getMenuPermissions(user.getUserId());
        SysRoleXEntity roleById = roleService.getById(user.getRoleId());

        Role role = new Role();
        if(roleById != null) {
            Set<Long> deptIdSet = StrUtil.split(roleById.getDeptIdSet(), ",").stream().map(Convert::toLong)
                .collect(Collectors.toSet());

            role = new Role(roleById.getRoleId(), roleById.getDataScope(), deptIdSet);
        }

        LoginUser loginUser = new LoginUser(user.getUserId(), user.getUsername(), user.getDeptId(), roleKeys,
            menuPermissions, user.getPassword(), role);

        loginUser.setEntity(user);

        return loginUser;
    }

    /**
     * 获取角色数据权限
     * TODO  可以放在领域类  loginUser里
     * @param userId 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRoleKeys(Long userId) {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (LoginUser.isAdmin(userId)) {
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
    public Set<String> getMenuPermissions(Long userId) {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (LoginUser.isAdmin(userId)) {
            perms.add("*:*:*");
        } else {
            perms.addAll(userService.selectMenuPermsByUserId(userId));
        }
        return perms;
    }



}
