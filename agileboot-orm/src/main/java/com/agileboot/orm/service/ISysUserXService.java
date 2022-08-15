package com.agileboot.orm.service;

import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.result.SearchUserResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Set;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
public interface ISysUserXService extends IService<SysUserXEntity> {

    boolean checkDeptExistUser(Long deptId);

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    boolean checkUserNameUnique(String userName);

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserRoleGroup(Long userId);

    /**
     * 根据用户ID查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserPostGroup(Long userId);

    /**
     * 校验手机号码是否唯一
     * @return 结果
     */
    boolean checkPhoneUnique(String phone, Long userId);

    /**
     * 校验email是否唯一
     * @return 结果
     */
    boolean checkEmailUnique(String Email, Long userId);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectRolePermissionByUserId(Long userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUserXEntity getUserByUserName(String userName);

    Page<SysUserXEntity> selectAllocatedList(Long roleId, String username, String phoneNumber,
        Page<SysUserXEntity> page);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    Page<SysUserXEntity>  selectUnallocatedList(Long roleId, String username, String phoneNumber,
        Page<SysUserXEntity> page);

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    Page<SearchUserResult> selectUserList( Page<SearchUserResult> page, QueryWrapper<SearchUserResult> queryWrapper);

    /**
     * 校验用户是否允许操作
     */
    void checkUserAllowed(Long userId);

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    void checkUserDataScope(Long userId);


    /**
     * 用户授权角色
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    void insertUserAuth(Long userId, Long[] roleIds);

}
