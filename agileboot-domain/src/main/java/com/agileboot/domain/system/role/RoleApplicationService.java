package com.agileboot.domain.system.role;

import cn.hutool.core.collection.CollUtil;
import com.agileboot.orm.entity.SysRoleDeptXEntity;
import com.agileboot.orm.entity.SysRoleMenuXEntity;
import com.agileboot.orm.entity.SysUserRoleXEntity;
import com.agileboot.orm.service.ISysRoleDeptXService;
import com.agileboot.orm.service.ISysRoleMenuXService;
import com.agileboot.orm.service.ISysRoleXService;
import com.agileboot.orm.service.ISysUserRoleXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleApplicationService {

    @Autowired
    private ISysRoleXService roleService;

    @Autowired
    private ISysRoleMenuXService roleMenuService;

    @Autowired
    private ISysRoleDeptXService roleDeptService;

    @Autowired
    private ISysUserRoleXService userRoleService;

    public boolean createRole(RoleModel roleModel) {
        roleModel.insert();
        List<SysRoleMenuXEntity> list = new ArrayList<>();
        for (Long menuId : roleModel.getMenuIds()) {
            SysRoleMenuXEntity rm = new SysRoleMenuXEntity();
            rm.setRoleId(roleModel.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }

        roleMenuService.saveBatch(list);
        return true;
    }


    public boolean updateRole(RoleModel roleModel) {
        roleModel.updateById();

        // 清空之前的角色菜单关联
        roleMenuService.getBaseMapper().deleteByMap(Collections.singletonMap("role_id", roleModel.getRoleId()));

        List<SysRoleMenuXEntity> list = new ArrayList<>();
        for (Long menuId : roleModel.getMenuIds()) {
            SysRoleMenuXEntity rm = new SysRoleMenuXEntity();
            rm.setRoleId(roleModel.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }

        roleMenuService.saveBatch(list);
        return true;
    }



    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public boolean authDataScope(RoleModel role) {
        // 新增角色和部门信息（数据权限）
        role.updateById();

        roleDeptService.getBaseMapper().deleteByMap(Collections.singletonMap("role_id", role.getRoleId()));
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



    public boolean deleteAuthUser(Long roleId, Long userId) {
        QueryWrapper<SysUserRoleXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", userId)
            .eq("user_id", userId);

        return userRoleService.remove(queryWrapper);
    }

    public boolean deleteAuthUsers(Long roleId, List<Long> userIds) {
        QueryWrapper<SysUserRoleXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId)
            .in("user_id", userIds);

        return userRoleService.remove(queryWrapper);
    }

    public boolean insertAuthUsers(Long roleId, List<Long> userIds) {
        if(CollUtil.isEmpty(userIds)) {
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


}
