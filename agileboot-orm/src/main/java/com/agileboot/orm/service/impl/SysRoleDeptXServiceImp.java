package com.agileboot.orm.service.impl;

import com.agileboot.orm.mapper.SysRoleDeptXMapper;
import com.agileboot.orm.po.SysRoleDeptXEntity;
import com.agileboot.orm.service.ISysRoleDeptXService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色和部门关联表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Service
public class SysRoleDeptXServiceImp extends ServiceImpl<SysRoleDeptXMapper, SysRoleDeptXEntity> implements
    ISysRoleDeptXService {

}
