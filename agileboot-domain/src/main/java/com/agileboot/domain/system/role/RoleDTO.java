package com.agileboot.domain.system.role;

import com.agileboot.common.annotation.ExcelColumn;
import com.agileboot.common.annotation.ExcelSheet;
import com.agileboot.orm.entity.SysRoleXEntity;
import java.util.Date;
import lombok.Data;

@Data
@ExcelSheet(name = "角色列表")
public class RoleDTO {

    public RoleDTO(SysRoleXEntity entity) {
        if (entity != null) {
            this.roleId = entity.getRoleId() + "";
            this.roleName = entity.getRoleName() + "";
            this.roleKey = entity.getRoleKey();
            this.roleSort = entity.getRoleSort() + "";
            this.createTime = entity.getCreateTime();
        }
    }

    @ExcelColumn(name = "角色ID")
    private String roleId;
    @ExcelColumn(name = "角色名称")
    private String roleName;
    @ExcelColumn(name = "角色标识")
    private String roleKey;
    @ExcelColumn(name = "角色排序")
    private String roleSort;
    @ExcelColumn(name = "创建时间")
    private Date createTime;

}
