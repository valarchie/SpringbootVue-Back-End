package com.agileboot.infrastructure.web.domain.login;

import com.agileboot.orm.entity.SysUserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 登录用户身份权限
 * TODO 这里面的类字段稍微改一下
 * @author valarchie
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    private String password;

    private String username;

    /**
     * 权限列表
     */
    private Set<String> menuPermissions;

    private Set<String> roleKeys;

    private SysUserEntity entity;

//  TODO  优化  这些大量的字符串 没必要都存 缓存 因为很多都是一样的
    private Role role;

    public LoginUser(Set<String> permissions) {
        this.menuPermissions = permissions;
    }

    public LoginUser(Long userId, String username, Long deptId, Set<String> roleKeys, Set<String> permissions,
        String password, Role role) {
        this.username = username;
        this.userId = userId;
        this.deptId = deptId;
        this.menuPermissions = permissions;
        this.roleKeys = roleKeys;
        this.password = password;
        this.role = role;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }



    /**
     * 账户是否未过期,过期无法验证
     * 未实现此功能
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 指定用户是否解锁,锁定的用户无法进行身份验证
     * 未实现此功能
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     * 未实现此功能
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否可用 ,禁用的用户不能身份验证
     * 未实现此功能
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * 是否为管理员
     * @return 结果
     */
    public boolean isAdmin() {
        return isAdmin(userId);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

}
