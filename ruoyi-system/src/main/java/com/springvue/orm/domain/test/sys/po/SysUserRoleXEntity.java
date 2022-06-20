package com.springvue.orm.domain.test.sys.po;

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
 * 用户和角色关联表
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Getter
@Setter
@TableName("sys_user_role")
@ApiModel(value = "SysUserRoleXEntity对象", description = "用户和角色关联表")
public class SysUserRoleXEntity extends Model<SysUserRoleXEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @ApiModelProperty("角色ID")
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;


    @Override
    public Serializable pkVal() {
        return this.roleId;
    }

}
