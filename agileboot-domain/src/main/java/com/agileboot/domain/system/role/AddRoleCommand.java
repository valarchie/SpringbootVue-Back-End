package com.agileboot.domain.system.role;

import cn.hutool.core.convert.Convert;
import com.agileboot.common.annotation.ExcelColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class AddRoleCommand {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    private String roleName;

    /**
     * 角色权限
     */
    @ExcelColumn(name = "角色权限")
    @NotBlank(message = "权限字符不能为空")
    @Size(max = 100, message = "权限字符长度不能超过100个字符")
    private String roleKey;

    /**
     * 角色排序
     */
    @ExcelColumn(name = "角色排序")
    @NotBlank(message = "显示顺序不能为空")
    private String roleSort;


    @ExcelColumn(name = "数据范围")
    private String dataScope;

    @PositiveOrZero
    protected String status;

    public RoleModel toModel() {
        RoleModel model = new RoleModel();

        model.setRoleName(this.roleName);
        model.setRoleKey(this.roleKey);
        model.setRoleSort(Convert.toInt(this.roleSort));
        model.setDataScope(Convert.toInt(this.dataScope));
        model.setStatus(Convert.toInt(status));

        return model;
    }

}
