package com.agileboot.infrastructure.cache.guava;


import com.agileboot.orm.service.ISysConfigXService;
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

    public GuavaCacheTemplate<String> configCache = new GuavaCacheTemplate<String>() {
        @Override
        public String getObjectFromDb(Object id) {
            return configXService.getConfigValueByKey(id.toString());
        }
    };








}
