package com.agileboot.orm.service;

import com.agileboot.orm.entity.SysRoleXEntity;
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
     * @return 结果
     */
    boolean checkRoleNameUnique(Long roleId, String roleName);

    /**
     * 校验角色权限是否唯一
     * @return 结果
     */
    boolean checkRoleKeyUnique(Long roleId, String roleKey);


    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    void checkRoleAllowed(Long roleId);


    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Deprecated
    List<SysRoleXEntity> selectRolesByUserId(Long userId);
}
