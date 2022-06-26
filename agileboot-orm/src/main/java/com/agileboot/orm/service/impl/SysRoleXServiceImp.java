package com.agileboot.orm.service.impl;

import com.agileboot.common.exception.ServiceException;
import com.agileboot.orm.deprecated.domain.SysUserRole;
import com.agileboot.orm.deprecated.entity.SysRole;
import com.agileboot.orm.entity.SysRoleDeptXEntity;
import com.agileboot.orm.entity.SysRoleMenuXEntity;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.entity.SysUserRoleXEntity;
import com.agileboot.orm.mapper.SysRoleXMapper;
import com.agileboot.orm.mapper.SysUserXMapper;
import com.agileboot.orm.service.ISysRoleDeptXService;
import com.agileboot.orm.service.ISysRoleMenuXService;
import com.agileboot.orm.service.ISysRoleXService;
import com.agileboot.orm.service.ISysUserRoleXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public boolean checkRoleNameUnique(SysRole role) {
        QueryWrapper<SysRoleXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(role.getRoleId() != null, "role_id", role.getRoleId())
            .eq("role_name", role.getRoleName());
        return this.count(queryWrapper)>0;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        QueryWrapper<SysRoleXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(role.getRoleId() != null, "role_id", role.getRoleId())
            .eq("role_key", role.getRoleName());
        return this.count(queryWrapper)>0;
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean insertRole(SysRole role) {
        // 新增角色信息
        SysRoleXEntity entity = role.toEntity();
        entity.insert();
        return insertRoleMenu(role);
    }

    @Override
    public void checkRoleAllowed(SysRole role) {
        if (role.getRoleId() != null && role.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    @Override
    public boolean updateRole(SysRole role) {
        // 新增角色信息
        SysRoleXEntity entity = role.toEntity();
        entity.updateById();
        return updateRoleMenu(role);
    }


    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public boolean insertRoleMenu(SysRole role) {
        // 新增用户与角色管理
        List<SysRoleMenuXEntity> list = new ArrayList<>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenuXEntity rm = new SysRoleMenuXEntity();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }

        return roleMenuService.saveBatch(list);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public boolean updateRoleMenu(SysRole role) {
        // 新增用户与角色管理
        List<SysRoleMenuXEntity> list = new ArrayList<>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenuXEntity rm = new SysRoleMenuXEntity();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }

        roleMenuService.getBaseMapper().deleteByMap(Collections.singletonMap("role_id", role.getRoleId()));
        return roleMenuService.saveBatch(list);
    }


    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    @Override
    public boolean insertRoleDept(SysRole role) {
        // 新增用户与角色管理
        List<SysRoleDeptXEntity> list = new ArrayList<>();
        for (Long deptId : role.getDeptIds()) {
            SysRoleDeptXEntity link = new SysRoleDeptXEntity();
            link.setRoleId(role.getRoleId());
            link.setDeptId(deptId);
            list.add(link);
        }

        return roleDeptService.saveBatch(list);
    }



    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean authDataScope(SysRole role) {
        SysRoleXEntity entity = role.toEntity();
        entity.updateById();

        roleDeptService.getBaseMapper().deleteByMap(Collections.singletonMap("role_id", role.getRoleId()));
        // 新增角色和部门信息（数据权限）
        return insertRoleDept(role);
    }

    @Override
    public boolean updateRoleStatus(SysRole role) {
        SysRoleXEntity entity = role.toEntity();
        return entity.updateById();
    }


    @Override
    public boolean deleteAuthUser(SysUserRole userRole) {
        QueryWrapper<SysUserRoleXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", userRole.getRoleId())
            .eq("user_id", userRole.getUserId());

       return userRoleService.remove(queryWrapper);
    }

    @Override
    public boolean deleteAuthUsers(Long roleId, Long[] userIds) {
        List<Long> userIdList = Arrays.stream(userIds).collect(Collectors.toList());

        QueryWrapper<SysUserRoleXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId)
            .in("user_id", userIdList);

        return userRoleService.remove(queryWrapper);
    }

    @Override
    public boolean insertAuthUsers(Long roleId, Long[] userIds) {
        if(userIds == null) {
            return false;
        }

        List<SysUserRoleXEntity> list = new ArrayList<>();

        for (Long userId : userIds) {
            SysUserRoleXEntity entity = new SysUserRoleXEntity();
            entity.setUserId(userId);
            entity.setRoleId(roleId);
            list.add(entity);
        }
        return userRoleService.saveBatch(list);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        List<SysRoleXEntity> sysRoleXEntities = userXMapper.selectRolePermissionByUserId(userId);

        List<SysRole> userRoles = sysRoleXEntities.stream().map(SysRole::new).collect(Collectors.toList());
        List<SysRole> roles = this.list().stream().map(SysRole::new).collect(Collectors.toList());
        for (SysRole role : roles) {
            for (SysRole userRole : userRoles) {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }


}
