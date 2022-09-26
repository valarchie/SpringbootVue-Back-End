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
 * 岗位信息表
 * </p>
 *
 * @author valarchie
 * @since 2022-07-02
 */
@Getter
@Setter
@TableName("sys_post")
@ApiModel(value = "SysPostXEntity对象", description = "岗位信息表")
public class SysPostEntity extends Model<SysPostEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("岗位ID")
    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;

    @ApiModelProperty("岗位编码")
    @TableField("post_code")
    private String postCode;

    @ApiModelProperty("岗位名称")
    @TableField("post_name")
    private String postName;

    @ApiModelProperty("显示顺序")
    @TableField("post_sort")
    private Integer postSort;

    @ApiModelProperty("状态（0正常 1停用）")
    @TableField("`status`")
    private Integer status;

    @TableField("creator_id")
    private Long creatorId;

    @ApiModelProperty("创建者")
    @TableField("create_name")
    private String createName;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField("updater_id")
    private Long updaterId;

    @ApiModelProperty("更新者")
    @TableField("update_name")
    private String updateName;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("逻辑删除")
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;


    @Override
    public Serializable pkVal() {
        return this.postId;
    }

}
