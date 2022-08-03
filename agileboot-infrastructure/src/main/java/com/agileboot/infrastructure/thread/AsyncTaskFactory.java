package com.agileboot.infrastructure.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.agileboot.common.enums.LoginStatusEnum;
import com.agileboot.common.utils.ServletHolderUtil;
import com.agileboot.common.utils.ip.AddressUtils;
import com.agileboot.orm.entity.SysLoginInfoXEntity;
import com.agileboot.orm.entity.SysOperationLogXEntity;
import com.agileboot.orm.service.ISysLoginInfoXService;
import com.agileboot.orm.service.ISysOperationLogXService;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;

/**
 * 异步工厂（产生任务用）
 *
 * @author ruoyi
 */
@Slf4j
public class AsyncTaskFactory {


    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param loginStatusEnum 状态
     * @param message 消息
     * @return 任务task
     */
    public static Runnable loginInfoTask(final String username, final LoginStatusEnum loginStatusEnum, final String message) {
        // 优化一下这个类
        final UserAgent userAgent = UserAgent.parseUserAgentString(
            ServletHolderUtil.getRequest().getHeader("User-Agent"));
        // 获取客户端浏览器
        final String browser = userAgent.getBrowser() != null ? userAgent.getBrowser().getName() : "";
        final String ip = ServletUtil.getClientIP(ServletHolderUtil.getRequest());
        final String address = AddressUtils.getRealAddressByIp(ip);
        // 获取客户端操作系统
        final String os = userAgent.getOperatingSystem() != null ? userAgent.getOperatingSystem().getName() : "";

        log.info("ip: {}, address: {}, username: {}, loginStatusEnum: {}, message: {}", ip, address, username,
            loginStatusEnum, message);
        return () -> {
            // 封装对象
            SysLoginInfoXEntity loginInfo = new SysLoginInfoXEntity();
            loginInfo.setUserName(username);
            loginInfo.setIpAddress(ip);
            loginInfo.setLoginLocation(address);
            loginInfo.setBrowser(browser);
            loginInfo.setOs(os);
            loginInfo.setMsg(message);
            loginInfo.setLoginTime(DateUtil.date());
            // 日志状态 TODO 替换魔法值
            loginInfo.setStatus(loginStatusEnum.getStatus());
            // 插入数据
            SpringUtil.getBean(ISysLoginInfoXService.class).save(loginInfo);
        };
    }

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static Runnable recordOperationLog(final SysOperationLogXEntity operLog) {
        return () -> {
            // 远程查询操作地点
            operLog.setOperatorLocation(AddressUtils.getRealAddressByIp(operLog.getOperatorIp()));
            SpringUtil.getBean(ISysOperationLogXService.class).save(operLog);
        };
    }

}
