package com.agileboot.orm.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.agileboot.orm.entity.SysConfigXEntity;
import com.agileboot.orm.mapper.SysConfigXMapper;
import com.agileboot.orm.service.ISysConfigXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 参数配置表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-09
 */
@Service
public class SysConfigXServiceImp extends ServiceImpl<SysConfigXMapper, SysConfigXEntity> implements
    ISysConfigXService {

    @Override
    public String getConfigValueByKey(String key) {
        if (StrUtil.isBlank(key)) {
            return StrUtil.EMPTY;
        }
        QueryWrapper<SysConfigXEntity> sysNoticeWrapper = new QueryWrapper<>();
        sysNoticeWrapper.eq("config_key", key);
        SysConfigXEntity one = this.getOne(sysNoticeWrapper);
        if (one == null || one.getConfigValue() == null) {
            return StrUtil.EMPTY;
        }
        return one.getConfigValue();
    }

    @Override
    public boolean isCaptchaOn() {
        QueryWrapper<SysConfigXEntity> sysNoticeWrapper = new QueryWrapper<>();
        sysNoticeWrapper.eq("config_key", "sys.account.captchaOnOff");
        SysConfigXEntity one = this.getOne(sysNoticeWrapper);
        if(one == null) {
            throw new RuntimeException("can not find config of captcha.");
        }
        return Convert.toBool(one.getConfigValue());
    }

}
