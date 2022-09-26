package com.agileboot.orm.service.impl;

//import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.orm.entity.SysRoleEntity;
import com.agileboot.orm.mapper.SysRoleMapper;
import com.agileboot.orm.mapper.SysUserMapper;
import com.agileboot.orm.service.ISysRoleMenuService;
import com.agileboot.orm.service.ISysRoleService;
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
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRoleEntity> implements ISysRoleService {

    @Autowired
    ISysRoleMenuService roleMenuService;

    @Autowired
    SysUserMapper userXMapper;


    @Override
    public boolean checkRoleDataScope(Long roleId) {
        // implement later
        return true;
    }

    @Override
    public boolean checkRoleNameUnique(Long roleId, String roleName) {
        QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(roleId != null, "role_id", roleId)
            .eq("role_name", roleName);
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public boolean checkRoleKeyUnique(Long roleId, String roleKey) {
        QueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(roleId != null, "role_id", roleId)
            .eq("role_key", roleKey);
        return this.baseMapper.exists(queryWrapper);
    }


    @Override
    public void checkRoleAllowed(Long roleId) {
        // TODO 不应该实现在这里
//        if (AuthenticationUtils.isAdminRole(roleId)) {
//            throw new ApiException(BusinessErrorCode.FORBIDDEN_TO_MODIFY_ADMIN);
//        }
    }

    @Override
    public List<SysRoleEntity> selectRolesByUserId(Long userId) {
        List<SysRoleEntity> roleEntities = userXMapper.selectRolesByUserId(userId);
        Set<Long> allRoleIdSet = this.list().stream().map(SysRoleEntity::getRoleId).collect(Collectors.toSet());

        return roleEntities.stream().filter(o -> allRoleIdSet.contains(o.getRoleId()))
            .collect(Collectors.toList());
    }


}
