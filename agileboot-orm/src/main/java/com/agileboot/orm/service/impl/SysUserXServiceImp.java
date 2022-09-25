package com.agileboot.orm.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.agileboot.orm.entity.SysPostXEntity;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.mapper.SysUserXMapper;
import com.agileboot.orm.query.AbstractPageQuery;
import com.agileboot.orm.result.SearchUserDO;
import com.agileboot.orm.service.ISysConfigXService;
import com.agileboot.orm.service.ISysUserXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Service
public class SysUserXServiceImp extends ServiceImpl<SysUserXMapper, SysUserXEntity> implements ISysUserXService {

    @Autowired
    private ISysConfigXService configService;

    @Autowired
    protected Validator validator;


    @Override
    public boolean checkDeptExistUser(Long deptId) {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dept_id", deptId);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean checkExistUserLinkToRole(Long roleId) {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean checkUserNameUnique(String userName) {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        return this.baseMapper.exists(queryWrapper);
    }


    @Override
    public String selectUserRoleGroup(Long userId) {
        List<SysRoleXEntity> list = baseMapper.selectRolesByUserId(userId);
        if (CollUtil.isEmpty(list)) {
            return StrUtil.EMPTY;
        }
        return list.stream().map(SysRoleXEntity::getRoleName).collect(Collectors.joining(","));
    }

    @Override
    public String selectUserPostGroup(Long userId) {
        List<SysPostXEntity> list = baseMapper.selectPostsByUserId(userId);
        if (CollUtil.isEmpty(list)) {
            return StrUtil.EMPTY;
        }
        return list.stream().map(SysPostXEntity::getPostName).collect(Collectors.joining(","));
    }

    @Override
    public boolean checkPhoneUnique(String phone, Long userId) {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(userId != null, "user_id", userId)
            .eq("phone_number", phone);
        return baseMapper.exists(queryWrapper);
    }

    @Override
    public boolean checkEmailUnique(String email, Long userId) {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(userId != null, "user_id", userId)
            .eq("email", email);
        return baseMapper.exists(queryWrapper);
    }

    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRoleXEntity> perms = baseMapper.selectRolesByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRoleXEntity perm : perms) {
            if (perm != null) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        Set<String> permissionStrList = baseMapper.selectMenuPermsByUserId(userId);
        Set<String> singlePermsSet = new HashSet<>();
        for (String perm : permissionStrList) {
            if (StrUtil.isNotEmpty(perm)) {
                singlePermsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return singlePermsSet;
    }

    @Override
    public SysUserXEntity getUserByUserName(String userName) {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName);
        return this.getOne(queryWrapper);
    }

    @Override
    public Page<SysUserXEntity> selectAllocatedList(AbstractPageQuery query) {
        Page<SysUserXEntity> page = query.toPage();
        List<SysUserXEntity> list = baseMapper.selectRoleAssignedUserList(page, query.toQueryWrapper());
        page.setRecords(list);
        return page;
    }

    @Override
    public Page<SysUserXEntity> selectUnallocatedList(AbstractPageQuery query) {
        Page<SysUserXEntity> page = query.toPage();
        List<SysUserXEntity> list = baseMapper.selectRoleUnassignedUserList(page, query.toQueryWrapper());
        page.setRecords(list);
        return page;
    }

    @Override
    public Page<SearchUserDO> selectUserList(AbstractPageQuery query) {
        Page page = query.toPage();
        List<SearchUserDO> searchUserDOS = baseMapper.selectUserList(page, query.toQueryWrapper());
        return page;
    }



    @Override
    public void checkUserAllowed(Long userId) {
        // 后面再实现
//        if (user.getUserId() != null && user.isAdmin()) {
//            throw new ServiceException("不允许操作超级管理员用户");
//        }
    }

    @Override
    public void checkUserDataScope(Long userId) {
        // TODO 后面再实现
//        if (!SysUser.isAdmin(AuthenticationUtils.getUserId())) {
//            SysUser user = new SysUser();
//            user.setUserId(userId);
//            List<SysUser> users = ((SysUserXServiceImp) AopContext.currentProxy()).selectUserList(user);
//            if (CollUtil.isEmpty(users)) {
//                throw new ServiceException("没有权限访问用户数据！");
//            }
//        }
    }



}
