package com.agileboot.orm.service.impl;

import com.agileboot.orm.mapper.SysUserPostXMapper;
import com.agileboot.orm.po.SysUserPostXEntity;
import com.agileboot.orm.service.ISysUserPostXService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与岗位关联表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Service
public class SysUserPostXServiceImp extends ServiceImpl<SysUserPostXMapper, SysUserPostXEntity> implements
    ISysUserPostXService {

}
