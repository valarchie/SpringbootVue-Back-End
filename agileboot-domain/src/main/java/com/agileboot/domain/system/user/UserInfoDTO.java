package com.agileboot.domain.system.user;

import com.agileboot.domain.system.role.RoleDTO;
import java.util.List;
import lombok.Data;

@Data
public class UserInfoDTO {

    private UserDTO user;
    private List<RoleDTO> roles;

}
