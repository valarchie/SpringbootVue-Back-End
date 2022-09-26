package com.agileboot.orm.service;

import com.agileboot.orm.entity.SysDeptEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
public interface ISysDeptService extends IService<SysDeptEntity> {


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
