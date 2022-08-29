package com.agileboot.admin.deprecated.domain;

import com.agileboot.common.annotation.ExcelColumn;
import com.agileboot.common.core.dto.BaseEntity;
import com.agileboot.orm.entity.SysLoginInfoXEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Deprecated
public class SysLogininfor extends BaseEntity {

    public SysLogininfor(SysLoginInfoXEntity loginInfoX) {
        if(loginInfoX!=null) {
            this.infoId = loginInfoX.getInfoId();
            this.userName = loginInfoX.getUsername();
            this.ipaddr = loginInfoX.getIpAddress();
            this.loginLocation = loginInfoX.getLoginLocation();
            this.browser = loginInfoX.getBrowser();
            this.os = loginInfoX.getOperationSystem();
            this.status = loginInfoX.getStatus()+"";
            this.msg = loginInfoX.getMsg();
            this.loginTime = loginInfoX.getLoginTime();
        }
    }

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
//    @ExcelColumn(name = "序号", cellType = ColumnType.NUMERIC)
    private Long infoId;

    /**
     * 用户账号
     */
    @ExcelColumn(name = "用户账号")
    private String userName;

    /**
     * 登录状态 0成功 1失败
     */
//    @ExcelColumn(name = "登录状态", readConverterExp = "0=成功,1=失败")
    private String status;

    /**
     * 登录IP地址
     */
    @ExcelColumn(name = "登录地址")
    private String ipaddr;

    /**
     * 登录地点
     */
    @ExcelColumn(name = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @ExcelColumn(name = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @ExcelColumn(name = "操作系统")
    private String os;

    /**
     * 提示消息
     */
    @ExcelColumn(name = "提示消息")
    private String msg;

    /**
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

}
