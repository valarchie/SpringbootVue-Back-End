package com.agileboot.orm.service.impl;

import com.agileboot.orm.mapper.SysUserRoleXMapper;
import com.agileboot.orm.po.SysUserRoleXEntity;
import com.agileboot.orm.service.ISysUserRoleXService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户和角色关联表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Service
public class SysUserRoleXServiceImp extends ServiceImpl<SysUserRoleXMapper, SysUserRoleXEntity> implements
    ISysUserRoleXService {

}
