package com.agileboot.orm.service.impl;

import com.agileboot.common.core.exception.ApiException;
import com.agileboot.common.core.exception.errors.BusinessErrorCode;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.mapper.SysRoleXMapper;
import com.agileboot.orm.mapper.SysUserXMapper;
import com.agileboot.orm.service.ISysRoleDeptXService;
import com.agileboot.orm.service.ISysRoleMenuXService;
import com.agileboot.orm.service.ISysRoleXService;
import com.agileboot.orm.service.ISysUserRoleXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色信息表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Service
public class SysRoleXServiceImp extends ServiceImpl<SysRoleXMapper, SysRoleXEntity> implements ISysRoleXService {

    @Autowired
    ISysRoleMenuXService roleMenuService;
    @Autowired
    ISysRoleDeptXService roleDeptService;
    @Autowired
    ISysUserRoleXService userRoleService;
    @Autowired
    SysUserXMapper userXMapper;


    @Override
    public boolean checkRoleDataScope(Long roleId) {
        // implement later
        return true;
    }

    @Override
    public boolean checkRoleNameUnique(Long roleId, String roleName) {
        QueryWrapper<SysRoleXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(roleId != null, "role_id", roleId)
            .eq("role_name", roleName);
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public boolean checkRoleKeyUnique(Long roleId, String roleKey) {
        QueryWrapper<SysRoleXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(roleId != null, "role_id", roleId)
            .eq("role_key", roleKey);
        return this.baseMapper.exists(queryWrapper);
    }


    @Override
    public void checkRoleAllowed(Long roleId) {
        if (AuthenticationUtils.isAdminRole(roleId)) {
            throw new ApiException(BusinessErrorCode.FORBIDDEN_TO_MODIFY_ADMIN);
        }
    }

    @Override
    public List<SysRoleXEntity> selectRolesByUserId(Long userId) {
        List<SysRoleXEntity> roleEntities = userXMapper.selectRolesByUserId(userId);
        Set<Long> allRoleIdSet = this.list().stream().map(SysRoleXEntity::getRoleId).collect(Collectors.toSet());

        return roleEntities.stream().filter(o -> allRoleIdSet.contains(o.getRoleId()))
            .collect(Collectors.toList());
    }


}
