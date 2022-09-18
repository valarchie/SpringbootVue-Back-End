package com.agileboot.admin.response;

import com.agileboot.domain.system.user.UserDTO;
import java.util.Set;
import lombok.Data;

@Data
public class UserPermissionDTO {

    private UserDTO user;
    private Set<String> roles;
    private Set<String> permissions;

}
