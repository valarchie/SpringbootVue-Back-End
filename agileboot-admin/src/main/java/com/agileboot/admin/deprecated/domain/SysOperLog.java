package com.agileboot.admin.deprecated.domain;

import cn.hutool.core.convert.Convert;
import com.agileboot.common.annotation.ExcelColumn;
import com.agileboot.common.core.dto.BaseEntity;
import com.agileboot.orm.entity.SysOperationLogXEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 操作日志记录表 oper_log
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Deprecated
public class SysOperLog extends BaseEntity {

    public SysOperLog(SysOperationLogXEntity entity) {
        this.operId = entity.getOperationId();
        this.businessType = entity.getBusinessType();
        this.deptName = entity.getDeptName();
        this.errorMsg = entity.getErrorStack();
        this.jsonResult = entity.getOperationResult();
        this.method = entity.getCalledMethod();
        this.operatorType = entity.getOperatorType();
        this.operIp = entity.getOperatorIp();
        this.operLocation = entity.getOperatorLocation();
        this.operName = entity.getUserName();
        this.operParam = entity.getOperationParam();
        this.operTime = entity.getOperationTime();
        this.operUrl = entity.getRequestUrl();
        this.requestMethod = Convert.toStr(entity.getRequestMethod());
        this.status = entity.getStatus();
        this.title = entity.getRequestModule();
    }



    private static final long serialVersionUID = 1L;

    /**
     * 日志主键
     */
//    @ExcelColumn(name = "操作序号", cellType = ColumnType.NUMERIC)
    private Long operId;

    /**
     * 操作模块
     */
    @ExcelColumn(name = "操作模块")
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
//    @ExcelColumn(name = "业务类型", readConverterExp = "0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据")
    private Integer businessType;

    /**
     * 业务类型数组
     */
    private Integer[] businessTypes;

    /**
     * 请求方法
     */
    @ExcelColumn(name = "请求方法")
    private String method;

    /**
     * 请求方式
     */
    @ExcelColumn(name = "请求方式")
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
//    @ExcelColumn(name = "操作类别", readConverterExp = "0=其它,1=后台用户,2=手机端用户")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @ExcelColumn(name = "操作人员")
    private String operName;

    /**
     * 部门名称
     */
    @ExcelColumn(name = "部门名称")
    private String deptName;

    /**
     * 请求url
     */
    @ExcelColumn(name = "请求地址")
    private String operUrl;

    /**
     * 操作地址
     */
    @ExcelColumn(name = "操作地址")
    private String operIp;

    /**
     * 操作地点
     */
    @ExcelColumn(name = "操作地点")
    private String operLocation;

    /**
     * 请求参数
     */
    @ExcelColumn(name = "请求参数")
    private String operParam;

    /**
     * 返回参数
     */
    @ExcelColumn(name = "返回参数")
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
//    @ExcelColumn(name = "状态", readConverterExp = "0=正常,1=异常")
    private Integer status;

    /**
     * 错误消息
     */
    @ExcelColumn(name = "错误消息")
    private String errorMsg;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @ExcelColumn(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

}
