package com.agileboot.orm.service.impl;

import com.agileboot.orm.entity.SysLoginInfoXEntity;
import com.agileboot.orm.mapper.SysLoginInfoXMapper;
import com.agileboot.orm.service.ISysLoginInfoXService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统访问记录 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-06
 */
@Service
public class SysLoginInfoXServiceImp extends ServiceImpl<SysLoginInfoXMapper, SysLoginInfoXEntity> implements
    ISysLoginInfoXService {

}
