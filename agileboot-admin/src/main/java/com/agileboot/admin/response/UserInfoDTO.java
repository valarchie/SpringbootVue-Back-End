package com.agileboot.admin.response;

import com.agileboot.admin.deprecated.entity.SysRole;
import com.agileboot.admin.deprecated.entity.SysUser;
import java.util.List;
import lombok.Data;

@Data
public class UserInfoDTO {

    private SysUser user;
    private List<SysRole> roles;

}
