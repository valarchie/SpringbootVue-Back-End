package com.ruoyi.system.domain.test.sys.po;

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
 * 字典数据表
 * </p>
 *
 * @author valarchie
 * @since 2022-06-13
 */
@Getter
@Setter
@TableName("sys_dict_data")
@ApiModel(value = "SysDictDataXEntity对象", description = "字典数据表")
public class SysDictDataXEntity extends Model<SysDictDataXEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("字典编码")
    @TableId(value = "dict_data_id", type = IdType.AUTO)
    private Long dictDataId;

    @ApiModelProperty("字典排序")
    @TableField("dict_sort")
    private Integer dictSort;

    @ApiModelProperty("字典标签")
    @TableField("dict_label")
    private String dictLabel;

    @ApiModelProperty("字典键值")
    @TableField("dict_value")
    private String dictValue;

    @ApiModelProperty("字典类型")
    @TableField("dict_type")
    private String dictType;

    @ApiModelProperty("样式属性（其他样式扩展）")
    @TableField("css_class")
    private String cssClass;

    @ApiModelProperty("列表回显样式")
    @TableField("list_class")
    private String listClass;

    @ApiModelProperty("图标")
    @TableField("icon_class")
    private String iconClass;

    @ApiModelProperty("是否默认（1是 0否）")
    @TableField("is_default")
    private Boolean isDefault;

    @ApiModelProperty("状态（0正常 1停用）")
    @TableField("`status`")
    private Boolean status;

    @ApiModelProperty("创建者ID")
    @TableField("creator_id")
    private Long creatorId;

    @ApiModelProperty("创建者")
    @TableField("create_name")
    private String createName;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新者ID")
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
    private Integer deleted;


    @Override
    public Serializable pkVal() {
        return this.dictDataId;
    }

}
