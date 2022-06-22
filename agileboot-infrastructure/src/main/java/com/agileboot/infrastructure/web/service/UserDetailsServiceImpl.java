package com.agileboot.infrastructure.web.service;

import com.agileboot.common.enums.UserStatus;
import com.agileboot.common.exception.ServiceException;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.orm.deprecated.entity.SysUser;
import com.agileboot.orm.service.ISysUserXService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private SysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUserName(username);
        if (user == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("登录用户：" + username + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        Set<String> roleKeys = userService.selectRolePermissionByUserId(user.getUserId());

        return new LoginUser(user.getUserId(), user.getDeptId(), roleKeys,
            permissionService.getMenuPermission(user.getUserId()));
    }


}
