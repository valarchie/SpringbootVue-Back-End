package com.agileboot.orm.entity;

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
 * 用户与岗位关联表
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Getter
@Setter
@TableName("sys_user_post")
@ApiModel(value = "SysUserPostXEntity对象", description = "用户与岗位关联表")
public class SysUserPostXEntity extends Model<SysUserPostXEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @ApiModelProperty("岗位ID")
    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;


    @Override
    public Serializable pkVal() {
        return this.postId;
    }

}
