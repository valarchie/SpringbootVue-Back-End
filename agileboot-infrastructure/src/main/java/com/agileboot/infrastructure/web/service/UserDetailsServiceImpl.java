package com.agileboot.infrastructure.web.service;

import com.agileboot.common.core.exception.ApiException;
import com.agileboot.common.core.exception.errors.BusinessErrorCode;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.enums.common.UserStatusEnum;
import com.agileboot.orm.service.ISysUserXService;
import java.util.Objects;
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
        SysUserXEntity user = userService.getUserByUserName(username);
        if (user == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new ApiException(BusinessErrorCode.USER_NON_EXIST, username);
        }
        if (!Objects.equals(UserStatusEnum.NORMAL.getValue(), user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ApiException(BusinessErrorCode.USER_IS_DISABLE, username);
        }
        Set<String> roleKeys = userService.selectRolePermissionByUserId(user.getUserId());

        return new LoginUser(user.getUserId(),user.getUsername(), user.getDeptId(), roleKeys,
            permissionService.getMenuPermission(user.getUserId()), user.getPassword());
    }


}
