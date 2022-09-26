package com.agileboot.orm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author valarchie
 * @since 2022-07-02
 */
@Getter
@Setter
@TableName("sys_dept")
@ApiModel(value = "SysDeptXEntity对象", description = "部门表")
public class SysDeptEntity extends Model<SysDeptEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("部门id")
    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;

    @ApiModelProperty("父部门id")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty("祖级列表")
    @TableField("ancestors")
    private String ancestors;

    @ApiModelProperty("部门名称")
    @TableField("dept_name")
    private String deptName;

    @ApiModelProperty("显示顺序")
    @TableField("order_num")
    private Integer orderNum;

    @TableField("leader_id")
    private Long leaderId;

    @ApiModelProperty("负责人")
    @TableField("leader_name")
    private String leaderName;

    @ApiModelProperty("联系电话")
    @TableField("phone")
    private String phone;

    @ApiModelProperty("邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty("部门状态（0正常 1停用）")
    @TableField("`status`")
    private Integer status;

    @ApiModelProperty("创建者ID")
    @TableField("creator_id")
    private Long creatorId;

    @ApiModelProperty("创建者")
    @TableField("creator_name")
    private String creatorName;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新者ID")
    @TableField("updater_id")
    private Long updaterId;

    @ApiModelProperty("更新者")
    @TableField("updater_name")
    private String updaterName;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("逻辑删除")
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;


    @Override
    public Serializable pkVal() {
        return this.deptId;
    }

}
