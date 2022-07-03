package com.agileboot.orm.mapper;

import com.agileboot.orm.entity.SysDeptXEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
public interface SysDeptXMapper extends BaseMapper<SysDeptXEntity> {

    String SELECT_DEPT_LIST_SQL = "SELECT d.dept_id  "
        + "FROM sys_dept d  "
        + "LEFT JOIN sys_role_dept rd ON d.dept_id = rd.dept_id  ";

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @param deptCheckStrictly 部门树选择项是否关联显示
     * @return 选中部门列表
     */
    @Select(SELECT_DEPT_LIST_SQL
        + "${ew.customSqlSegment}")
    List<Long> selectDeptListByRoleId(Long roleId);


    List<Long> selectDeptListByRoleId(Long roleId, boolean is);

}
