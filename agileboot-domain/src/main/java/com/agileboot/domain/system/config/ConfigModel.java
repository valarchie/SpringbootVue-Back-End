package com.agileboot.domain.system.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.orm.entity.SysConfigEntity;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Data;

@Data
public class ConfigModel extends SysConfigEntity {

    private Set<String> configOptionSet;

    public ConfigModel(SysConfigEntity entity) {
        BeanUtil.copyProperties(entity, this);

        List<?> options =
            JSONUtil.isTypeJSONArray(entity.getConfigOptions()) ? JSONUtil.toList(entity.getConfigOptions(),
                String.class) : ListUtil.empty();

        this.configOptionSet = new HashSet(options);
    }

    public void editConfigValue(String value) {
        if (StrUtil.isBlank(value)) {
            throw new ApiException(BusinessErrorCode.CONFIG_VALUE_IS_NOT_ALLOW_TO_EMPTY);
        }

        if(!configOptionSet.isEmpty()&& !configOptionSet.contains(value)) {
            throw new ApiException(BusinessErrorCode.CONFIG_VALUE_IS_NOT_IN_OPTIONS);
        }

        if(!Objects.equals(value, getConfigValue())) {
            this.setConfigValue(value);
            this.updateById();
        }
    }


}
