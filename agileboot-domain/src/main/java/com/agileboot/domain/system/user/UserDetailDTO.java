package com.agileboot.domain.system.user;

import com.agileboot.domain.system.post.PostDTO;
import com.agileboot.domain.system.role.RoleDTO;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class UserDetailDTO {

    private UserDTO user;

    private List<RoleDTO> roles;

    private List<PostDTO> posts;

    private List<Long> postIds;

    private List<Long> roleIds;

    private Set<String> permissions;

}
