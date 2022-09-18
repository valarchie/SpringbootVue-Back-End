package com.agileboot.domain.system.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**
 * @author valarchie
 */
public class UserProfileDTO {

    private UserDTO user;
    private String roleGroup;
    private String postGroup;

}
