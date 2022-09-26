package com.agileboot.infrastructure.cache.guava;


import com.agileboot.orm.entity.SysDeptEntity;
import com.agileboot.orm.service.ISysConfigService;
import com.agileboot.orm.service.ISysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author valarchie
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
@Slf4j
public class GuavaCacheService {

    @Autowired
    private ISysConfigService configXService;

    @Autowired
    private ISysDeptService deptService;

    public GuavaCacheTemplate<String> configCache = new GuavaCacheTemplate<String>() {
        @Override
        public String getObjectFromDb(Object id) {
            return configXService.getConfigValueByKey(id.toString());
        }
    };

    public GuavaCacheTemplate<SysDeptEntity> deptCache = new GuavaCacheTemplate<SysDeptEntity>() {
        @Override
        public SysDeptEntity getObjectFromDb(Object id) {
            return deptService.getById(id.toString());
        }
    };








}
