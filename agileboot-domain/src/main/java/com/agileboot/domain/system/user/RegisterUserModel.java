package com.agileboot.domain.system.user;

import com.agileboot.orm.entity.SysUserXEntity;
import lombok.Data;

@Data
public class RegisterUserModel extends SysUserXEntity {

    private String code;
    private String uuid;

}
