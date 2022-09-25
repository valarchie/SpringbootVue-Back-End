package com.agileboot.domain.system.role;

import cn.hutool.core.collection.CollUtil;
import com.agileboot.common.core.dto.PageDTO;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.domain.system.user.UserDTO;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.service.TokenService;
import com.agileboot.infrastructure.web.service.UserDetailsServiceImpl;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.service.ISysRoleMenuXService;
import com.agileboot.orm.service.ISysRoleXService;
import com.agileboot.orm.service.ISysUserXService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleDomainService {

    @Autowired
    private ISysRoleXService roleService;

    @Autowired
    private ISysRoleMenuXService roleMenuService;

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public PageDTO getRoleList(RoleQuery query) {
        Page<SysRoleXEntity> page = roleService.page(query.toPage(), query.toQueryWrapper());
        List<RoleDTO> records = page.getRecords().stream().map(RoleDTO::new).collect(Collectors.toList());
        return new PageDTO(records, page.getTotal());
    }

    public RoleDTO getRoleInfo(Long roleId) {
        SysRoleXEntity byId = roleService.getById(roleId);
        return new RoleDTO(byId);
    }


    public void addRole(AddRoleCommand addCommand, LoginUser loginUser) {
        RoleModel roleModel = addCommand.toModel();

        roleModel.checkRoleNameUnique(roleService);
        roleModel.checkRoleKeyUnique(roleService);

        roleModel.setCreatorId(loginUser.getUserId());
        roleModel.setCreatorName(loginUser.getUsername());

        roleModel.insert(roleMenuService);
    }

    public void deleteRoleByBulk(List<Long> roleIds, LoginUser loginUser) {
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                deleteRole(roleId, loginUser);
            }
        }
    }

    public void deleteRole(Long roleId, LoginUser loginUser) {
        RoleModel roleModel = getRoleModel(roleId);

        roleModel.checkRoleNameUnique(roleService);

        roleModel.setUpdaterId(loginUser.getUserId());
        roleModel.setUpdaterName(loginUser.getUsername());

        roleModel.deleteById(roleMenuService);
    }


    public void updateRole(UpdateRoleCommand updateCommand, LoginUser loginUser) {
        SysRoleXEntity byId = roleService.getById(updateCommand.getRoleId());

        if (byId == null) {
            throw new ApiException(BusinessErrorCode.OBJECT_NOT_FOUND, updateCommand.getRoleId(), "角色");
        }

        RoleModel roleModel = updateCommand.toModel();
        roleModel.checkRoleKeyUnique(roleService);
        roleModel.checkRoleNameUnique(roleService);

        roleModel.setUpdaterId(loginUser.getUserId());
        roleModel.setUpdaterName(loginUser.getUsername());

        roleModel.updateById(roleMenuService);

        if (loginUser.isAdmin()) {
            loginUser.setMenuPermissions(userDetailsService.getMenuPermissions(loginUser.getUserId()));
            tokenService.setLoginUser(loginUser);
        }
    }


    public RoleModel getRoleModel(Long roleId) {
        SysRoleXEntity byId = roleService.getById(roleId);

        if (byId == null) {
            throw new ApiException(BusinessErrorCode.OBJECT_NOT_FOUND, roleId, "角色");
        }

        return new RoleModel(byId);
    }

    public void updateStatus(UpdateStatusCommand command, LoginUser loginUser) {
        RoleModel roleModel = getRoleModel(command.getRoleId());
        roleModel.setStatus(command.getStatus());
        roleModel.setUpdaterId(loginUser.getUserId());
        roleModel.setUpdaterName(loginUser.getUsername());
        roleModel.updateById();
    }

    public void updateDataScope(UpdateDataScopeCommand command) {
        RoleModel roleModel = getRoleModel(command.getRoleId());
        roleModel.setDeptIds(command.getDeptIds());
        roleModel.setDataScope(command.getDataScope());

        roleModel.generateDeptIdSet();
        roleModel.updateById();
    }


    public PageDTO getAllocatedUserList(AllocatedRoleQuery query) {
        Page<SysUserXEntity> page = userService.selectAllocatedList(query);
        List<UserDTO> dtoList = page.getRecords().stream().map(UserDTO::new).collect(Collectors.toList());
        return new PageDTO(dtoList, page.getTotal());
    }

    public PageDTO getUnallocatedUserList(UnallocatedRoleQuery query) {
        Page<SysUserXEntity> page = userService.selectUnallocatedList(query);
        List<UserDTO> dtoList = page.getRecords().stream().map(UserDTO::new).collect(Collectors.toList());
        return new PageDTO(dtoList, page.getTotal());
    }



    public void deleteRoleOfUser(Long userId) {
        SysUserXEntity user = userService.getById(userId);
        if (user != null) {
            user.setRoleId(null);
            user.updateById();
        }
    }

    public void deleteRoleOfUserByBulk(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }

        for (Long userId : userIds) {
//            SysUserXEntity user = userService.getById(userId);

            LambdaUpdateWrapper<SysUserXEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(SysUserXEntity::getRoleId, null).eq(SysUserXEntity::getUserId, userId);

            userService.update(updateWrapper);
        }
    }

    public void addRoleOfUserByBulk(Long roleId, List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }

        for (Long userId : userIds) {
            SysUserXEntity user = userService.getById(userId);
            user.setRoleId(roleId);
            user.updateById();
        }
    }


}
