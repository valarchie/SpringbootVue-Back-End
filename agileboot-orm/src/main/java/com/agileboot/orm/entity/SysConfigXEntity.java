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
 * 参数配置表
 * </p>
 *
 * @author valarchie
 * @since 2022-06-09
 */
@Getter
@Setter
@TableName("sys_config")
@ApiModel(value = "SysConfigXEntity对象", description = "参数配置表")
public class SysConfigXEntity extends Model<SysConfigXEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("参数主键")
    @TableId(value = "config_id", type = IdType.AUTO)
    private Integer configId;

    @ApiModelProperty("配置名称")
    @TableField("config_name")
    private String configName;

    @ApiModelProperty("配置键名")
    @TableField("config_key")
    private String configKey;

    @ApiModelProperty("配置值")
    @TableField("config_value")
    private String configValue;

    @ApiModelProperty("配置可选参数")
    @TableField("config_options")
    private String configOptions;

    @ApiModelProperty("创建者ID")
    @TableField("creator_id")
    private Long creatorId;

    @ApiModelProperty("创建者")
    @TableField("creator_name")
    private String creatorName;

    @ApiModelProperty("更新者ID")
    @TableField("updater_id")
    private Long updaterId;

    @ApiModelProperty("更新者")
    @TableField("updater_name")
    private String updaterName;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("逻辑删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;


    @Override
    public Serializable pkVal() {
        return this.configId;
    }

}
