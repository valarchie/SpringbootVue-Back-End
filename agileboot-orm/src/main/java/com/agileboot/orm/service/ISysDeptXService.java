package com.agileboot.orm.service;

import cn.hutool.core.lang.tree.Tree;
import com.agileboot.orm.entity.SysDeptXEntity;
import com.baomidou.mybatisplus.extension.service.IService;
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
    List<Tree<Long>> buildDeptTree(List<SysDeptXEntity> depts);

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
    boolean checkDeptNameUnique(String deptName, Long deptId, Long parentId);

    boolean checkDeptDataScope(Long deptId);

    /**
     * 检测部门底下是否还有正在使用中的子部门
     * @param deptId
     * @return
     */
    boolean existChildrenDeptById(Long deptId, Boolean enabled);

    boolean isChildOfTargetDeptId(Long ancestorId, Long childId);

    boolean hasChildDeptById(Long deptId);

}
