package com.springvue.orm.domain.test.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springvue.orm.domain.test.sys.mapper.SysUserRoleXMapper;
import com.springvue.orm.domain.test.sys.po.SysUserRoleXEntity;
import com.springvue.orm.domain.test.sys.service.ISysUserRoleXService;
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
