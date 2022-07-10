package com.agileboot.admin.response;

import com.agileboot.admin.deprecated.entity.SysUser;
import java.util.Set;
import lombok.Data;

@Data
public class UserPermissionDTO {

    private SysUser user;
    private Set<String> roles;
    private Set<String> permissions;

}
