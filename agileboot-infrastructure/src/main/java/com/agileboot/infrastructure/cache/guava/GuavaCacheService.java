package com.agileboot.infrastructure.cache.guava;


import com.agileboot.orm.entity.SysDeptXEntity;
import com.agileboot.orm.service.ISysConfigXService;
import com.agileboot.orm.service.ISysDeptXService;
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
    private ISysConfigXService configXService;

    @Autowired
    private ISysDeptXService deptService;

    public GuavaCacheTemplate<String> configCache = new GuavaCacheTemplate<String>() {
        @Override
        public String getObjectFromDb(Object id) {
            return configXService.getConfigValueByKey(id.toString());
        }
    };

    public GuavaCacheTemplate<SysDeptXEntity> deptCache = new GuavaCacheTemplate<SysDeptXEntity>() {
        @Override
        public SysDeptXEntity getObjectFromDb(Object id) {
            return deptService.getById(id.toString());
        }
    };








}
