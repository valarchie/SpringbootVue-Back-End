package com.agileboot.domain.system.dept;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.domain.system.TreeSelectedDTO;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.util.AuthenticationUtils;
import com.agileboot.orm.entity.SysDeptXEntity;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.service.ISysDeptXService;
import com.agileboot.orm.service.ISysRoleXService;
import com.agileboot.orm.service.ISysUserXService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author valarchie
 */
@SuppressWarnings("AlibabaTransactionMustHaveRollback")
@Service
public class DeptDomainService {

    @Autowired
    private ISysDeptXService deptService;

    @Autowired
    private ISysRoleXService roleService;

    @Autowired
    private ISysUserXService userService;

    public List<DeptDTO> getDeptList(DeptQuery query) {
        List<SysDeptXEntity> list = deptService.list(query.toQueryWrapper());
        return list.stream().map(DeptDTO::new).collect(Collectors.toList());
    }

    public DeptDTO getDept(Long id) {
        SysDeptXEntity byId = deptService.getById(id);
        return new DeptDTO(byId);
    }

    public List<Tree<Long>> getDeptTree() {
        List<SysDeptXEntity> list = deptService.list();

        return TreeUtil.build(list, 0L, (dept, tree) -> {
            tree.setId(dept.getDeptId());
            tree.setParentId(dept.getParentId());
            tree.putExtra("label", dept.getDeptName());
        });
    }

    public TreeSelectedDTO getDeptTreeForRole(Long roleId) {
        List<Long> checkedKeys = new ArrayList<>();
        SysRoleXEntity role = roleService.getById(roleId);
        if (role != null && StrUtil.isNotEmpty(role.getDeptIdSet())) {
            checkedKeys = StrUtil.split(role.getDeptIdSet(), ",")
                .stream().map(Long::new).collect(Collectors.toList());
        }

        TreeSelectedDTO selectedDTO = new TreeSelectedDTO();
        selectedDTO.setDepts(getDeptTree());
        selectedDTO.setCheckedKeys(checkedKeys);

        return selectedDTO;
    }


    @Transactional
    public void addDept(AddDeptCommand addCommand) {
        DeptModel deptModel = addCommand.toModel();
        if (deptService.checkDeptNameUnique(deptModel.getDeptName(), null, deptModel.getParentId())) {
            throw new ApiException(BusinessErrorCode.DEPT_NAME_IS_NOT_UNIQUE, deptModel.getDeptName());
        }

        deptModel.generateAncestors(deptService);

        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        deptModel.setCreatorId(loginUser.getUserId());
        deptModel.setCreatorName(loginUser.getUsername());
        deptModel.insert();
    }

    @Transactional
    public void updateDept(UpdateDeptCommand updateCommand) {
        // TODO 需要再调整一下
        getDeptModel(updateCommand.getDeptId());

        DeptModel deptModel = updateCommand.toModel();
        if (deptService.checkDeptNameUnique(deptModel.getDeptName(), deptModel.getDeptId(), deptModel.getParentId())) {
            throw new ApiException(BusinessErrorCode.DEPT_NAME_IS_NOT_UNIQUE, deptModel.getDeptName());
        }

        deptModel.checkParentId();
        deptModel.checkStatusAllowChange(deptService);
        deptModel.generateAncestors(deptService);

        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        deptModel.setUpdaterId(loginUser.getUserId());
        deptModel.setUpdaterName(loginUser.getUsername());

        deptModel.updateById();
    }

    @Transactional
    public void removeDept(Long deptId) {
        DeptModel deptModel = getDeptModel(deptId);

        deptModel.checkExistChildDept(deptService);
        deptModel.checkExistLinkedUsers(userService);

        deptService.removeById(deptId);
    }

    public DeptModel getDeptModel(Long id) {
        SysDeptXEntity byId = deptService.getById(id);

        if (byId == null) {
            throw new ApiException(BusinessErrorCode.OBJECT_NOT_FOUND, id, "参数配置");
        }

        return new DeptModel(byId);
    }

}
