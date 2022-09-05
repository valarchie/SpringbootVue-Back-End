package com.agileboot.admin.controller.system;

import cn.hutool.core.lang.tree.Tree;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.domain.system.TreeSelectedDTO;
import com.agileboot.domain.system.dept.AddDeptCommand;
import com.agileboot.domain.system.dept.DeptDTO;
import com.agileboot.domain.system.dept.DeptDomainService;
import com.agileboot.domain.system.dept.DeptQuery;
import com.agileboot.domain.system.dept.UpdateDeptCommand;
import com.agileboot.infrastructure.annotations.AccessLog;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部门信息
 *
 * @author valarchie
 */
@RestController
@RequestMapping("/system/dept")
@Validated
public class SysDeptController extends BaseController {

    @Autowired
    private DeptDomainService deptDomainService;

    /**
     * 获取部门列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public ResponseDTO list(DeptQuery query) {
        List<DeptDTO> deptList = deptDomainService.getDeptList(query);
        return ResponseDTO.ok(deptList);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public ResponseDTO excludeCurrentDept(@PathVariable(value = "deptId", required = false) Long deptId) {
        DeptQuery query = new DeptQuery();
        query.setDeptId(deptId);
        query.setExcludeCurrentDept(true);

        List<DeptDTO> deptList = deptDomainService.getDeptList(query);
        return ResponseDTO.ok(deptList);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public ResponseDTO<DeptDTO> getInfo(@PathVariable Long deptId) {
        DeptDTO dept = deptDomainService.getDept(deptId);
        return ResponseDTO.ok(dept);
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/dropdownList")
    public ResponseDTO<List> dropdownList() {
        List<Tree<Long>> deptTree = deptDomainService.getDeptTree();
        return ResponseDTO.ok(deptTree);
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/dropdownList/role/{roleId}")
    public ResponseDTO dropdownListForRole(@PathVariable("roleId") Long roleId) {
        TreeSelectedDTO deptTreeForRole = deptDomainService.getDeptTreeForRole(roleId);
        return ResponseDTO.ok(deptTreeForRole);
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @AccessLog(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@RequestBody AddDeptCommand addCommand) {
        deptDomainService.addDept(addCommand);
        return ResponseDTO.ok();
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit') AND @ss.checkDataScopeWithDeptId(#updateCommand.deptId)")
    @AccessLog(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@RequestBody UpdateDeptCommand updateCommand) {
        deptDomainService.updateDept(updateCommand);
        return ResponseDTO.ok();
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove') AND @ss.checkDataScopeWithDeptId(#deptId)")
    @AccessLog(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public ResponseDTO remove(@PathVariable @NotNull Long deptId) {
        deptDomainService.removeDept(deptId);
        return ResponseDTO.ok();
    }
}
