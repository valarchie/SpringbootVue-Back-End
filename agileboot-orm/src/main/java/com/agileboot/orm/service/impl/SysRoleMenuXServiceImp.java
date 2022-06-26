package com.agileboot.orm.service.impl;

import com.agileboot.orm.entity.SysRoleMenuXEntity;
import com.agileboot.orm.mapper.SysRoleMenuXMapper;
import com.agileboot.orm.service.ISysRoleMenuXService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色和菜单关联表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Service
public class SysRoleMenuXServiceImp extends ServiceImpl<SysRoleMenuXMapper, SysRoleMenuXEntity> implements
    ISysRoleMenuXService {

}
