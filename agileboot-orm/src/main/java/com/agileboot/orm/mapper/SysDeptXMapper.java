package com.agileboot.orm.mapper;

import com.agileboot.orm.po.SysDeptXEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
public interface SysDeptXMapper extends BaseMapper<SysDeptXEntity> {

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @param deptCheckStrictly 部门树选择项是否关联显示
     * @return 选中部门列表
     */
    List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId,
        @Param("deptCheckStrictly") boolean deptCheckStrictly);

}
