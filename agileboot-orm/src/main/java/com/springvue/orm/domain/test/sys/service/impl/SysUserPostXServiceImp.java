package com.springvue.orm.domain.test.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springvue.orm.domain.test.sys.mapper.SysUserPostXMapper;
import com.springvue.orm.domain.test.sys.po.SysUserPostXEntity;
import com.springvue.orm.domain.test.sys.service.ISysUserPostXService;
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
