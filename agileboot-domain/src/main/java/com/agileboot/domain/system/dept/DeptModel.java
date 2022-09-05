package com.agileboot.domain.system.dept;

import cn.hutool.core.bean.BeanUtil;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.orm.entity.SysDeptXEntity;
import com.agileboot.orm.enums.dictionary.CommonStatusEnum;
import com.agileboot.orm.enums.interfaces.BasicEnumUtil;
import com.agileboot.orm.service.ISysDeptXService;
import com.agileboot.orm.service.ISysUserXService;
import java.util.Objects;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeptModel extends SysDeptXEntity {

    public DeptModel(SysDeptXEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity, this);
        }
    }


    public void checkParentId() {
        if (Objects.equals(getParentId(), getDeptId())) {
            throw new ApiException(BusinessErrorCode.DEPT_PARENT_ID_IS_NOT_ALLOWED_SELF);
        }
    }


    public void checkExistChildDept(ISysDeptXService deptService) {
        if (deptService.hasChildDeptById(getDeptId())) {
            throw new ApiException(BusinessErrorCode.DEPT_EXIST_CHILD_DEPT_NOT_ALLOW_DELETE);
        }
    }

    public void checkExistLinkedUsers(ISysUserXService userService) {
        if (userService.checkDeptExistUser(getDeptId())) {
            throw new ApiException(BusinessErrorCode.DEPT_EXIST_LINK_USER_NOT_ALLOW_DELETE);
        }
    }

    public void generateAncestors(ISysDeptXService deptService) {
        SysDeptXEntity parentDept = deptService.getById(getParentId());

        if (parentDept == null || CommonStatusEnum.DISABLE.equals(
            BasicEnumUtil.fromValue(CommonStatusEnum.class, parentDept.getStatus()))) {
            throw new ApiException(BusinessErrorCode.DEPT_PARENT_DEPT_NO_EXIST_OR_DISABLED);
        }

        setAncestors(parentDept.getAncestors() + "," + getParentId());
    }


    /**
     * DDD 有些阻抗  如果为了追求性能的话  还是得通过 数据库的方式来判断
     * @param deptService
     */
    public void checkStatusAllowChange(ISysDeptXService deptService) {
        if (CommonStatusEnum.DISABLE.getValue().equals(getStatus()) &&
            deptService.existChildrenDeptById(getDeptId(), true)) {
            throw new ApiException(BusinessErrorCode.DEPT_STATUS_ID_IS_NOT_ALLOWED_CHANGE);
        }

    }

}
