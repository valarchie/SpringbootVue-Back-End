package com.agileboot.orm.mapper;

import com.agileboot.orm.entity.SysPostXEntity;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.result.SearchUserResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;

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


    List<SysUserXEntity> selectAllocatedList(Page<SysUserXEntity> page,
        @Param("queryConditions") Wrapper<SysUserXEntity> queryWrapper);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @return 用户信息集合信息
     */
    List<SysUserXEntity> selectUnallocatedList(Page<SysUserXEntity> page,
        @Param("queryConditions") Wrapper<SysUserXEntity> queryWrapper);

    /**
     * 根据条件分页查询用户列表
     *
     * @return 用户信息集合信息
     */
    List<SearchUserResult> selectUserList(Page<SearchUserResult> page,
        @Param("queryConditions") Wrapper<SearchUserResult> queryWrapper);

}
