package com.springvue.orm.domain.test.sys.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 系统访问记录
 * </p>
 *
 * @author valarchie
 * @since 2022-06-06
 */
@Getter
@Setter
@TableName("sys_login_info")
@ApiModel(value = "SysLoginInfoXEntity对象", description = "系统访问记录")
public class SysLoginInfoXEntity extends Model<SysLoginInfoXEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("访问ID")
    @TableId(value = "info_id", type = IdType.AUTO)
    private Long infoId;

    @ApiModelProperty("用户账号")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("登录IP地址")
    @TableField("ip_address")
    private String ipAddress;

    @ApiModelProperty("登录地点")
    @TableField("login_location")
    private String loginLocation;

    @ApiModelProperty("浏览器类型")
    @TableField("browser")
    private String browser;

    @ApiModelProperty("操作系统")
    @TableField("os")
    private String os;

    @ApiModelProperty("登录状态（0成功 1失败）")
    @TableField("`status`")
    private Integer status;

    @ApiModelProperty("提示消息")
    @TableField("msg")
    private String msg;

    @ApiModelProperty("访问时间")
    @TableField("login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    @ApiModelProperty("逻辑删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;


    @Override
    public Serializable pkVal() {
        return this.infoId;
    }

}
