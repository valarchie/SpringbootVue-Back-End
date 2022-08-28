package com.agileboot.domain.system.config;

import com.agileboot.common.core.dto.PageDTO;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.orm.entity.SysConfigXEntity;
import com.agileboot.orm.service.ISysConfigXService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author valarchie
 */
@Service
public class ConfigDomainService {

    @Autowired
    private ISysConfigXService configService;

    public PageDTO getConfigList(ConfigQuery query) {
        Page<SysConfigXEntity> page = configService.page(query.toPage(), query.toQueryWrapper());
        List<ConfigDTO> records = page.getRecords().stream().map(ConfigDTO::new).collect(Collectors.toList());
        return new PageDTO(records, page.getTotal());
    }


    public ConfigDTO getConfig(Long id) {
        SysConfigXEntity byId = configService.getById(id);
        return new ConfigDTO(byId);
    }

    public void updateConfig(ConfigUpdateDTO updateDTO) {
        ConfigModel configModel = getConfigModel(updateDTO.getConfigId());
        configModel.editConfigValue(updateDTO.getConfigValue());
    }



    public ConfigModel getConfigModel(Long id) {
        SysConfigXEntity byId = configService.getById(id);

        if (byId == null) {
            throw new ApiException(BusinessErrorCode.OBJECT_NOT_FOUND, id, "参数配置");
        }

        return new ConfigModel(byId);
    }





}
