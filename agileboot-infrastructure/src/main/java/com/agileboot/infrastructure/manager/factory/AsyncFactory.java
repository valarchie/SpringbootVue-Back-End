package com.agileboot.infrastructure.manager.factory;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.utils.ServletHolderUtil;
import com.agileboot.common.utils.ip.AddressUtils;
import com.agileboot.orm.po.SysLoginInfoXEntity;
import com.agileboot.orm.po.SysOperationLogXEntity;
import com.agileboot.orm.service.ISysLoginInfoXService;
import com.agileboot.orm.service.ISysOperationLogXService;
import eu.bitwalker.useragentutils.UserAgent;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步工厂（产生任务用）
 *
 * @author ruoyi
 */
public class AsyncFactory {

    private static final Logger log = LoggerFactory.getLogger("sys-user");

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status 状态
     * @param message 消息
     * @param args 列表
     * @return 任务task
     */
    public static TimerTask recordLoginInfo(final String username, final String status, final String message,
        final Object... args) {
        // 优化一下这个类
        final UserAgent userAgent = UserAgent.parseUserAgentString(
            ServletHolderUtil.getRequest().getHeader("User-Agent"));
        final String ip = ServletUtil.getClientIP(ServletHolderUtil.getRequest());
        return new TimerTask() {
            @Override
            public void run() {
                String address = AddressUtils.getRealAddressByIp(ip);
                StringBuilder s = new StringBuilder();
                s.append(getBlock(ip));
                s.append(address);
                s.append(getBlock(username));
                s.append(getBlock(status));
                s.append(getBlock(message));
                // 打印信息到日志
                log.info(s.toString(), args);
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName();
                // 封装对象
                SysLoginInfoXEntity logininfor = new SysLoginInfoXEntity();
                logininfor.setUserName(username);
                logininfor.setIpAddress(ip);
                logininfor.setLoginLocation(address);
                logininfor.setBrowser(browser);
                logininfor.setOs(os);
                logininfor.setMsg(message);
                logininfor.setLoginTime(DateUtil.date());
                // 日志状态 TODO 替换魔法值
                if (StrUtil.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
                    logininfor.setStatus(1);
                } else if (Constants.LOGIN_FAIL.equals(status)) {
                    logininfor.setStatus(0);
                }
                // 插入数据
                SpringUtil.getBean(ISysLoginInfoXService.class).save(logininfor);
            }
        };
    }

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOperationLog(final SysOperationLogXEntity operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                // 远程查询操作地点
                operLog.setOperatorLocation(AddressUtils.getRealAddressByIp(operLog.getOperatorIp()));
                SpringUtil.getBean(ISysOperationLogXService.class).save(operLog);
            }
        };
    }

    private static String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg + "]";
    }
}
