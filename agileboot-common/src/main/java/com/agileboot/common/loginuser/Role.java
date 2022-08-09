package com.agileboot.common.loginuser;

import java.util.Set;
import lombok.Data;

@Data
public class Role {

    private Long roleId;
    private Integer dataScope;
    private Set<Long> deptIdSet;

}
