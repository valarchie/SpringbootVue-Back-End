package com.agileboot.admin.controller.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.entity.SysDept;
import com.agileboot.common.annotation.Log;
import com.agileboot.common.constant.UserConstants;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.Rdto;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.orm.entity.SysDeptXEntity;
import com.agileboot.orm.service.ISysDeptXService;
import com.agileboot.orm.service.ISysUserXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
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
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {

    @Autowired
    private ISysDeptXService deptService;

    @Autowired
    private ISysUserXService userService;

    /**
     * 获取部门列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public Rdto list(SysDept dept) {
        Page<SysDeptXEntity> page = getPage();
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(dept.getStatus() != null, "status", dept.getStatus())
            .eq(dept.getDeptId() != null, "dept_id", dept.getDeptId())
            .eq(dept.getParentId() != null, "parent_id", dept.getParentId())
            .like(StrUtil.isNotEmpty(dept.getDeptName()), "dept_name", dept.getDeptName());

        deptService.page(page, queryWrapper);
        return Rdto.success(page.getRecords());
    }

    /**
     * 查询部门列表（排除节点）
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public Rdto excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(deptId != null, "dept_id", deptId)
            .apply(deptId != null, "dept_id = '" + deptId + "' or FIND_IN_SET ( dept_id , ancestors)");
        // in_set
        List<SysDeptXEntity> depts = deptService.list(queryWrapper);
//        Iterator<SysDeptXEntity> it = depts.iterator();
//        while (it.hasNext()) {
//            SysDeptXEntity d = it.next();
//            if (d.getDeptId().intValue() == deptId
//                || ArrayUtils.contains(CharSequenceUtil.splitToArray(d.getAncestors(), ","),
//                deptId + "")) {
//
//                it.remove();
//            }
//        }
        return Rdto.success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public Rdto getInfo(@PathVariable Long deptId) {
        // TODO 防止越权操作
        return Rdto.success(deptService.getById(deptId));
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    public Rdto treeSelect(SysDept dept) {

        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(dept.getStatus() != null, "status", dept.getStatus())
            .eq(dept.getDeptId() != null, "dept_id", dept.getDeptId())
            .eq(dept.getParentId() != null, "parent_id", dept.getParentId())
            .like(StrUtil.isNotEmpty(dept.getDeptName()), "dept_name", dept.getDeptName());

        List<SysDeptXEntity> list = deptService.list(queryWrapper);

        return Rdto.success(deptService.buildDeptTree(list));
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    public Rdto roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
        List<SysDeptXEntity> list = deptService.list();
        Rdto ajax = Rdto.success();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("depts", deptService.buildDeptTree(list));
        return ajax;
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Rdto add(@Validated @RequestBody SysDept dept) {
        if (deptService.checkDeptNameUnique(dept.getDeptName(), null, dept.getParentId())) {
            return Rdto.error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(getUsername());

        SysDeptXEntity entity = new SysDeptXEntity();
        entity.setParentId(dept.getParentId());
        entity.setAncestors(dept.getAncestors());
        entity.setDeptName(dept.getDeptName());
        entity.setOrderNum(dept.getOrderNum());
        entity.setLeaderName(dept.getLeader());
        entity.setPhone(dept.getPhone());
        entity.setEmail(dept.getEmail());
        entity.setCreatorId(getUserId());
        entity.setCreatorName(getUsername());

        return toAjax(entity.insert());
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Rdto edit(@Validated @RequestBody SysDept dept) {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (deptService.checkDeptNameUnique(dept.getDeptName(), dept.getDeptId(), dept.getParentId())) {
            return Rdto.error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        if (dept.getParentId().equals(deptId)) {
            return Rdto.error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        if (StrUtil.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
            && deptService.existEnabledChildrenDeptById(deptId)) {
            return Rdto.error("该部门包含未停用的子部门！");
        }

        SysDeptXEntity entity = new SysDeptXEntity();
        entity.setDeptId(deptId);
        entity.setParentId(dept.getParentId());
        entity.setAncestors(dept.getAncestors());
        entity.setDeptName(dept.getDeptName());
        entity.setOrderNum(dept.getOrderNum());
        entity.setLeaderName(dept.getLeader());
        entity.setPhone(dept.getPhone());
        entity.setEmail(dept.getEmail());
        entity.setUpdaterName(getUsername());
        entity.setUpdaterId(getUserId());
        return toAjax(entity.updateById());
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public Rdto remove(@PathVariable Long deptId) {
        if (deptService.hasChildDeptById(deptId)) {
            return Rdto.error("存在下级部门,不允许删除");
        }
        if (userService.checkDeptExistUser(deptId)) {
            return Rdto.error("部门存在用户,不允许删除");
        }
        deptService.checkDeptDataScope(deptId);
        return toAjax(deptService.removeById(deptId));
    }
}
