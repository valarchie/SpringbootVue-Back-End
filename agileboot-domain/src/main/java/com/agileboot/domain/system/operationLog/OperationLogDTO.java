package com.agileboot.domain.system.operationLog;

import cn.hutool.core.bean.BeanUtil;
import com.agileboot.common.annotation.ExcelSheet;
import com.agileboot.orm.entity.SysOperationLogXEntity;
import java.util.Date;
import lombok.Data;

@Data
@ExcelSheet(name = "登录日志")
public class OperationLogDTO {

    public OperationLogDTO(SysOperationLogXEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity, this);
        }
    }


    private Long operationId;

    private Integer businessType;

    private Integer requestMethod;

    private String requestModule;

    private String requestUrl;

    private String calledMethod;

    private Integer operatorType;

    private Long userId;

    private String userName;

    private String operatorIp;

    private String operatorLocation;

    private Long deptId;

    private String deptName;

    private String operationParam;

    private String operationResult;

    private Integer status;

    private String errorStack;

    private Date operationTime;

}
