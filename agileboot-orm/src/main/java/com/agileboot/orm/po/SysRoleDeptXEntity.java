package com.agileboot.orm.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色和部门关联表
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Getter
@Setter
@TableName("sys_role_dept")
@ApiModel(value = "SysRoleDeptXEntity对象", description = "角色和部门关联表")
public class SysRoleDeptXEntity extends Model<SysRoleDeptXEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("角色ID")
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    @ApiModelProperty("部门ID")
    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;


    @Override
    public Serializable pkVal() {
        return this.deptId;
    }

}
