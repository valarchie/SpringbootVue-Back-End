package com.agileboot.orm.mapper;

import com.agileboot.orm.entity.SysMenuXEntity;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
public interface SysMenuXMapper extends BaseMapper<SysMenuXEntity> {

    /**
     * 根据用户查询系统菜单列表
     * @param page
     * @param queryWrapper
     * @return
     */
    List<SysMenuXEntity> selectMenuListByUserId(Page<SysMenuXEntity> page, @Param("queryConditions") Wrapper<SysMenuXEntity> queryWrapper);


    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId,
        @Param("menuCheckStrictly") boolean menuCheckStrictly);

        /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenuXEntity> selectMenuTreeByUserId(Long userId);
}
