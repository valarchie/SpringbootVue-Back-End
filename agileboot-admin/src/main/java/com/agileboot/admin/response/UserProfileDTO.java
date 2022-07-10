package com.agileboot.admin.response;

import com.agileboot.admin.deprecated.entity.SysUser;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**
 * @author valarchie
 */
public class UserProfileDTO {

    private SysUser user;
    private String roleGroup;
    private String postGroup;

}
