package com.ruoyi.system.domain.test.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.domain.test.sys.po.SysDeptXEntity;
import java.util.List;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
public interface ISysDeptXService extends IService<SysDeptXEntity> {


    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    List<SysDeptXEntity> buildDeptTree(List<SysDeptXEntity> depts);

    List<TreeSelect> buildDeptTreeSelect(List<SysDeptXEntity> depts);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    List<Long> selectDeptListByRoleId(Long roleId);

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    boolean checkDeptNameUnique(SysDept dept);

    boolean checkDeptDataScope(Long deptId);

    long countEnabledChildrenDeptById(Long deptId);

    boolean hasChildDeptById(Long deptId);

}
