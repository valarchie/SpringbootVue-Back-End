package com.agileboot.domain.system.user.command;

import lombok.Data;

@Data
public class UpdateProfileCommand {

    private Long userId;

    private String phoneNumber;
    private String email;

}
