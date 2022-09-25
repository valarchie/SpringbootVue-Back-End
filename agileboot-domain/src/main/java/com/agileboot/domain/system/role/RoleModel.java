package com.agileboot.domain.system.role;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.orm.entity.SysRoleMenuXEntity;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.service.ISysRoleMenuXService;
import com.agileboot.orm.service.ISysRoleXService;
import com.agileboot.orm.service.ISysUserXService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleModel extends SysRoleXEntity {

    public RoleModel(SysRoleXEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity,this);
        }
    }


    private List<Long> menuIds;

    private List<Long> deptIds;

    public void checkRoleNameUnique(ISysRoleXService roleService) {
        if (roleService.checkRoleNameUnique(getRoleId(), getRoleName())) {
            throw new ApiException(BusinessErrorCode.ROLE_NAME_IS_NOT_UNIQUE, getRoleName());
        }
    }

    public void checkRoleCanBeDelete(ISysUserXService userService) {
        if (userService.checkExistUserLinkToRole(getRoleId())) {
            throw new ApiException(BusinessErrorCode.ROLE_NAME_IS_NOT_UNIQUE, getRoleName());
        }
    }

    public void checkRoleKeyUnique(ISysRoleXService roleService) {
        if (roleService.checkRoleKeyUnique(getRoleId(), getRoleKey())) {
            throw new ApiException(BusinessErrorCode.ROLE_KEY_IS_NOT_UNIQUE, getRoleKey());
        }
    }

    public void generateDeptIdSet() {
        if (deptIds == null) {
            setDeptIdSet("");
        }

        if (deptIds.size() > new HashSet<>(deptIds).size()) {
            throw new ApiException(BusinessErrorCode.ROLE_DATA_SCOPE_DUPLICATED_DEPT);
        }

        String deptIdSet = StrUtil.join(",", deptIds);
        setDeptIdSet(deptIdSet);
    }



    public void insert(ISysRoleMenuXService roleMenuService) {
        this.insert();
        saveMenus(roleMenuService);
    }

    public void updateById(ISysRoleMenuXService roleMenuService) {
        this.updateById();
        // 清空之前的角色菜单关联
        roleMenuService.getBaseMapper().deleteByMap(Collections.singletonMap("role_id", getRoleId()));
        saveMenus(roleMenuService);
    }

    public void deleteById(ISysRoleMenuXService roleMenuService) {
        this.deleteById();
        // 清空之前的角色菜单关联
        roleMenuService.getBaseMapper().deleteByMap(Collections.singletonMap("role_id", getRoleId()));
    }


    public void saveMenus(ISysRoleMenuXService roleMenuService) {
        List<SysRoleMenuXEntity> list = new ArrayList<>();
        if (getMenuIds() != null) {
            for (Long menuId : getMenuIds()) {
                SysRoleMenuXEntity rm = new SysRoleMenuXEntity();
                rm.setRoleId(getRoleId());
                rm.setMenuId(menuId);
                list.add(rm);
            }
            roleMenuService.saveBatch(list);
        }
    }

}
