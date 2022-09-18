package com.agileboot.orm.mapper;

import com.agileboot.orm.entity.SysPostXEntity;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.result.SearchUserDO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
     * @param userId 用户名
     * @return 角色列表
     */
    @Select("SELECT DISTINCT r.* "
        + "FROM sys_role r "
        + " LEFT JOIN sys_user_role ur ON ur.role_id = r.role_id "
        + " LEFT JOIN sys_user u ON u.user_id = ur.user_id "
        + "WHERE r.deleted = 0 "
        + " AND u.user_id = #{userId}")
    List<SysRoleXEntity> selectRolesByUserId(Long userId);

    /**
     * 查询用户所属岗位组
     *
     * @param userId 用户名
     * @return 结果
     */
    @Select("SELECT p.* "
        + "FROM sys_post p "
        + " LEFT JOIN sys_user_post up ON up.post_id = p.post_id "
        + " LEFT JOIN sys_user u ON u.user_id = up.user_id "
        + "WHERE u.user_id = #{userId} "
        + " AND p.deleted = 0")
    List<SysPostXEntity> selectPostsByUserId(Long userId);


    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("SELECT DISTINCT m.perms "
        + "FROM sys_menu m "
        + " LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id "
        + " LEFT JOIN sys_user_role ur ON rm.role_id = ur.role_id "
        + " LEFT JOIN sys_role r ON r.role_id = ur.role_id "
        + "WHERE m.status = 1 AND m.deleted = 0 "
        + " AND r.status = 1 AND r.deleted = 0 "
        + " AND ur.user_id = #{userId}")
    Set<String> selectMenuPermsByUserId(Long userId);


    @Select("SELECT DISTINCT u.user_id, u.dept_id, u.username, u.nick_name, u.email "
        + " , u.phone_number, u.status, u.create_time "
        + "FROM sys_user u "
        + " LEFT JOIN sys_dept d ON u.dept_id = d.dept_id "
        + " LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id "
        + " LEFT JOIN sys_role r ON r.role_id = ur.role_id "
        + " ${ew.customSqlSegment}")
    List<SysUserXEntity> selectRoleAssignedUserList(Page<SysUserXEntity> page,
        @Param(Constants.WRAPPER) Wrapper<SysUserXEntity> queryWrapper);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @return 用户信息集合信息
     */
    @Select("SELECT DISTINCT u.user_id, u.dept_id, u.username, u.nick_name, u.email "
        + " , u.phone_number, u.status, u.create_time "
        + "FROM sys_user u "
        + " LEFT JOIN sys_dept d ON u.dept_id = d.dept_id "
        + " LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id "
        + " LEFT JOIN sys_role r ON r.role_id = ur.role_id"
        + " ${ew.customSqlSegment}")
    List<SysUserXEntity> selectRoleUnassignedUserList(Page<SysUserXEntity> page,
        @Param(Constants.WRAPPER) Wrapper<SysUserXEntity> queryWrapper);

    /**
     * 根据条件分页查询用户列表
     *
     * @return 用户信息集合信息
     */
    @Select("SELECT u.*, d.dept_name, d.leader_name "
        + "FROM sys_user u "
        + " LEFT JOIN sys_dept d ON u.dept_id = d.dept_id "
        + "${ew.customSqlSegment}")
    List<SearchUserDO> selectUserList(Page<SearchUserDO> page,
        @Param(Constants.WRAPPER) Wrapper<SearchUserDO> queryWrapper);

}
