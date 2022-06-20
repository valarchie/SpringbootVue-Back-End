package com.springvue.orm.domain.test.sys.po;

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
 * 操作日志记录
 * </p>
 *
 * @author valarchie
 * @since 2022-06-08
 */
@Getter
@Setter
@TableName("sys_operation_log")
@ApiModel(value = "SysOperationLogXEntity对象", description = "操作日志记录")
public class SysOperationLogXEntity extends Model<SysOperationLogXEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("日志主键")
    @TableId(value = "operation_id", type = IdType.AUTO)
    private Long operationId;

    @ApiModelProperty("业务类型（0其它 1新增 2修改 3删除）")
    @TableField("business_type")
    private Integer businessType;

    @ApiModelProperty("请求方式")
    @TableField("request_method")
    private Integer requestMethod;

    @ApiModelProperty("请求模块")
    @TableField("request_module")
    private String requestModule;

    @ApiModelProperty("请求URL")
    @TableField("request_url")
    private String requestUrl;

    @ApiModelProperty("方法名称")
    @TableField("called_method")
    private String calledMethod;

    @ApiModelProperty("操作类别（0其它 1后台用户 2手机端用户）")
    @TableField("operator_type")
    private Integer operatorType;

    @ApiModelProperty("用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("操作人员")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("主机地址")
    @TableField("operator_ip")
    private String operatorIp;

    @ApiModelProperty("操作地点")
    @TableField("operator_location")
    private String operatorLocation;

    @ApiModelProperty("部门名称")
    @TableField("dept_id")
    private Long deptId;

    @TableField("dept_name")
    private String deptName;

    @ApiModelProperty("请求参数")
    @TableField("operation_param")
    private String operationParam;

    @ApiModelProperty("返回参数")
    @TableField("operation_result")
    private String operationResult;

    @ApiModelProperty("操作状态（1正常 0异常）")
    @TableField("`status`")
    private Integer status;

    @ApiModelProperty("错误消息")
    @TableField("error_stack")
    private String errorStack;

    @ApiModelProperty("操作时间")
    @TableField("operation_time")
    private Date operationTime;

    @ApiModelProperty("逻辑删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;


    @Override
    public Serializable pkVal() {
        return this.operationId;
    }

}
