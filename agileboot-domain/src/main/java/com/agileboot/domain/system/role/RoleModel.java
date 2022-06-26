package com.agileboot.domain.system.role;

import com.agileboot.orm.entity.SysRoleXEntity;
import java.util.List;
import lombok.Data;

@Data
public class RoleModel extends SysRoleXEntity {

    private List<Long> menuIds;

    private List<Long> deptIds;


}
