package com.agileboot.orm.service;

import com.agileboot.orm.deprecated.domain.SysUserRole;
import com.agileboot.orm.deprecated.entity.SysRole;
import com.agileboot.orm.po.SysRoleXEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 角色信息表 服务类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
public interface ISysRoleXService extends IService<SysRoleXEntity> {

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    boolean checkRoleDataScope(Long roleId);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean checkRoleKeyUnique(SysRole role);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean insertRole(SysRole role);

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    void checkRoleAllowed(SysRole role);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean updateRole(SysRole role);

    boolean insertRoleDept(SysRole role);

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean authDataScope(SysRole role);


    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean updateRoleStatus(SysRole role);


    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    boolean deleteAuthUser(SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    boolean deleteAuthUsers(Long roleId, Long[] userIds);


    /**
     * 批量选择授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    boolean insertAuthUsers(Long roleId, Long[] userIds);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserId(Long userId);
}
