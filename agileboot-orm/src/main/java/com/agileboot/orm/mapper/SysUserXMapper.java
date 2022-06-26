package com.agileboot.orm.mapper;

import com.agileboot.orm.deprecated.entity.SysUser;
import com.agileboot.orm.entity.SysPostXEntity;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.entity.SysUserXEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
public interface SysUserXMapper extends BaseMapper<SysUserXEntity> {

    /**
     * 根据用户ID查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    List<SysRoleXEntity> selectRolesByUserName(String userName);

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    List<SysPostXEntity> selectPostsByUserName(String userName);


    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRoleXEntity> selectRolePermissionByUserId(Long userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByUserId(Long userId);


    List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUserList(SysUser sysUser);

}
