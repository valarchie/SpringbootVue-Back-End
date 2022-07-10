package com.agileboot.admin.response;

import com.agileboot.admin.deprecated.domain.SysPost;
import com.agileboot.admin.deprecated.entity.SysRole;
import com.agileboot.admin.deprecated.entity.SysUser;
import java.util.List;
import lombok.Data;

@Data
public class UserDetailDTO {

    private SysUser user;

    private List<SysRole> roles;

    private List<SysPost> posts;

    private List<Long> postIds;

    private List<Long> roleIds;

}
