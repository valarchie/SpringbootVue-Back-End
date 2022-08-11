package com.agileboot.common.loginuser;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    private Long roleId;
    private Integer dataScope;
    private Set<Long> deptIdSet;

}
