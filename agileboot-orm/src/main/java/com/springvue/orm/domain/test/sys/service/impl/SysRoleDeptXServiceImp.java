package com.springvue.orm.domain.test.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springvue.orm.domain.test.sys.mapper.SysRoleDeptXMapper;
import com.springvue.orm.domain.test.sys.po.SysRoleDeptXEntity;
import com.springvue.orm.domain.test.sys.service.ISysRoleDeptXService;
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
public class SysRoleDeptXServiceImp extends ServiceImpl<SysRoleDeptXMapper, SysRoleDeptXEntity> implements ISysRoleDeptXService {

}
