package com.agileboot.orm.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.agileboot.common.exception.ServiceException;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.orm.deprecated.entity.SysUser;
import com.agileboot.orm.mapper.SysUserXMapper;
import com.agileboot.orm.po.SysPostXEntity;
import com.agileboot.orm.po.SysRoleXEntity;
import com.agileboot.orm.po.SysUserRoleXEntity;
import com.agileboot.orm.po.SysUserXEntity;
import com.agileboot.orm.service.ISysConfigXService;
import com.agileboot.orm.service.ISysUserRoleXService;
import com.agileboot.orm.service.ISysUserXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import org.springframework.aop.framework.AopContext;
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

    @Autowired
    private ISysUserRoleXService userRoleService;


    @Override
    public boolean checkDeptExistUser(Long deptId) {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dept_id", deptId);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public boolean checkUserNameUnique(String userName) {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        return this.count(queryWrapper) > 0;
    }


    @Override
    public String selectUserRoleGroup(String userName) {
        // TODO 记得xml的查询语句 要排除掉deleted = 1的 记录
        List<SysRoleXEntity> list = baseMapper.selectRolesByUserName(userName);
        if (CollUtil.isEmpty(list)) {
            return StrUtil.EMPTY;
        }
        return list.stream().map(SysRoleXEntity::getRoleName).collect(Collectors.joining(","));
    }

    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPostXEntity> list = baseMapper.selectPostsByUserName(userName);
        if (CollUtil.isEmpty(list)) {
            return StrUtil.EMPTY;
        }
        return list.stream().map(SysPostXEntity::getPostName).collect(Collectors.joining(","));
    }

    @Override
    public boolean checkPhoneUnique(SysUser user) {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(user.getUserId() != null, "user_id", user.getUserId())
            .eq("phone_number", user.getPhonenumber());
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean checkEmailUnique(SysUser user) {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(user.getUserId() != null, "user_id", user.getUserId())
            .eq("email", user.getEmail());
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRoleXEntity> perms = baseMapper.selectRolePermissionByUserId(userId);
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
        Set<String> perms = baseMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StrUtil.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public SysUser selectUserByUserName(String userName) {
        return baseMapper.selectUserByUserName(userName);
    }

    @Override
    public List<SysUser> selectAllocatedList(SysUser user) {
        return baseMapper.selectAllocatedList(user);
    }

    @Override
    public List<SysUser> selectUnallocatedList(SysUser user) {
        return null;
    }

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return baseMapper.selectUserList(user);
    }

    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (userList == null || userList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.getConfigValueByKey("sys.user.initPassword");
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = baseMapper.selectUserByUserName(user.getUserName());
                if (u == null) {
                    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(user);
                    if (!constraintViolations.isEmpty()) {
                        throw new ConstraintViolationException(constraintViolations);
                    }
                    user.setPassword(AuthenticationUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    SysUserXEntity entity = user.toEntity();
                    entity.insert();

                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                } else if (isUpdateSupport) {
                    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(user);
                    if (!constraintViolations.isEmpty()) {
                        throw new ConstraintViolationException(constraintViolations);
                    }
                    user.setUpdateBy(operName);
                    SysUserXEntity entity = user.toEntity();

                    entity.updateById();

                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName())
                        .append(" 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName())
                        .append(" 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    @Override
    public void checkUserAllowed(SysUser user) {
        if (user.getUserId() != null && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    @Override
    public void checkUserDataScope(Long userId) {
        if (!SysUser.isAdmin(AuthenticationUtils.getUserId())) {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = ((SysUserXServiceImp) AopContext.currentProxy()).selectUserList(user);
            if (CollUtil.isEmpty(users)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    @Override
    public void insertUserAuth(Long userId, Long[] roleIds) {

        userRoleService.getBaseMapper().deleteByMap(Collections.singletonMap("user_id", userId));

        List<SysUserRoleXEntity> list = new ArrayList<>();

        for (Long roleId : roleIds) {
            SysUserRoleXEntity entity = new SysUserRoleXEntity();
            entity.setUserId(userId);
            entity.setRoleId(roleId);

            list.add(entity);
        }
        userRoleService.saveBatch(list);
    }


}
