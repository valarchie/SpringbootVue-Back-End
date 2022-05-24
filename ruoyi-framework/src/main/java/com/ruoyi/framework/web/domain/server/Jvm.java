package com.ruoyi.framework.web.domain.server;

import static com.ruoyi.common.constant.Constants.KB;

import cn.hutool.core.util.NumberUtil;
import com.ruoyi.common.constant.Constants;
import java.lang.management.ManagementFactory;
import com.ruoyi.common.utils.DateUtils;

/**
 * JVM相关信息
 *
 * @author ruoyi
 */
public class Jvm {

    /**
     * 当前JVM占用的内存总数(M)
     */
    private double total;

    /**
     * JVM最大可用内存总数(M)
     */
    private double max;

    /**
     * JVM空闲内存(M)
     */
    private double free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    public double getTotal() {
        return NumberUtil.div(total, Constants.MB, 2);
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getMax() {
        return NumberUtil.div(max, Constants.MB, 2);
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getFree() {
        return NumberUtil.div(free, Constants.MB, 2);
    }

    public void setFree(double free) {
        this.free = free;
    }

    public double getUsed() {
        return NumberUtil.div(total - free, Constants.MB, 2);
    }

    public double getUsage() {
        return NumberUtil.div((total - free) * 100, total, 2);
    }

    /**
     * 获取JDK名称
     */
    public String getName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    /**
     * JDK启动时间
     */
    public String getStartTime() {
        return DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getServerStartDate());
    }

    /**
     * JDK运行时间
     */
    public String getRunTime() {
        return DateUtils.getDatePoor(DateUtils.getNowDate(), DateUtils.getServerStartDate());
    }

    /**
     * 运行参数
     */
    public String getInputArgs() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
    }
}
