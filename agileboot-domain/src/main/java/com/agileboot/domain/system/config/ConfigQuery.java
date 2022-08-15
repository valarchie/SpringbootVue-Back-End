package com.agileboot.domain.system.config;

import cn.hutool.core.util.StrUtil;
import com.agileboot.orm.entity.SysConfigXEntity;
import com.agileboot.orm.query.AbstractPageQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;

@Data
public class ConfigQuery extends AbstractPageQuery {

    private String configName;

    @SuppressWarnings("rawtypes")
    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper<SysConfigXEntity> sysNoticeWrapper = new QueryWrapper<>();
        sysNoticeWrapper.like(StrUtil.isNotEmpty(configName), "config_name", configName);
        return sysNoticeWrapper;
    }
}
