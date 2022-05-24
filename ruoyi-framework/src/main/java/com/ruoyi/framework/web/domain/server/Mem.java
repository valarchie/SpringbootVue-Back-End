package com.ruoyi.framework.web.domain.server;

import cn.hutool.core.util.NumberUtil;
import com.ruoyi.common.constant.Constants;

/**
 * 內存相关信息
 *
 * @author valarchie
 */
public class Mem
{
    /**
     * 内存总量
     */
    private double total;

    /**
     * 已用内存
     */
    private double used;

    /**
     * 剩余内存
     */
    private double free;

    public double getTotal()
    {
        return NumberUtil.div(total, Constants.GB, 2);
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public double getUsed()
    {
        return NumberUtil.div(used, Constants.GB, 2);
    }

    public void setUsed(long used)
    {
        this.used = used;
    }

    public double getFree()
    {
        return NumberUtil.div(free, Constants.GB, 2);
    }

    public void setFree(long free)
    {
        this.free = free;
    }

    public double getUsage()
    {
        return NumberUtil.div(used * 100, total, 2);
    }
}
