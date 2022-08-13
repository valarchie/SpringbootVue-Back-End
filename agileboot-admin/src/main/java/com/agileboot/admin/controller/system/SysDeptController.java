package com.agileboot.admin.controller.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.entity.SysDept;
import com.agileboot.admin.response.TreeSelectedDTO;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.util.AuthenticationUtils;
import com.agileboot.orm.entity.SysDeptXEntity;
import com.agileboot.orm.enums.dictionary.CommonStatusEnum;
import com.agileboot.orm.service.ISysDeptXService;
import com.agileboot.orm.service.ISysUserXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Objects;
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
    public ResponseDTO list(SysDept dept) {
        Page<SysDeptXEntity> page = getPage();
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(dept.getStatus() != null, "status", dept.getStatus())
            .eq(dept.getDeptId() != null, "dept_id", dept.getDeptId())
            .eq(dept.getParentId() != null, "parent_id", dept.getParentId())
            .like(StrUtil.isNotEmpty(dept.getDeptName()), "dept_name", dept.getDeptName());

        deptService.page(page, queryWrapper);
        return ResponseDTO.ok(page.getRecords());
    }

    /**
     * 查询部门列表（排除节点）
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public ResponseDTO excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
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
        return ResponseDTO.ok(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public ResponseDTO getInfo(@PathVariable Long deptId) {
        // TODO 防止越权操作
        return ResponseDTO.ok(deptService.getById(deptId));
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/dropdownList")
    public ResponseDTO dropdownList(SysDept dept) {

        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(dept.getStatus() != null, "status", dept.getStatus())
            .eq(dept.getDeptId() != null, "dept_id", dept.getDeptId())
            .eq(dept.getParentId() != null, "parent_id", dept.getParentId())
            .like(StrUtil.isNotEmpty(dept.getDeptName()), "dept_name", dept.getDeptName());

        List<SysDeptXEntity> list = deptService.list(queryWrapper);

        return ResponseDTO.ok(deptService.buildDeptTree(list));
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/dropdownList/role/{roleId}")
    public ResponseDTO dropdownListForRole(@PathVariable("roleId") Long roleId) {
        List<SysDeptXEntity> list = deptService.list();

        TreeSelectedDTO tree = new TreeSelectedDTO();
        tree.setCheckedKeys(deptService.selectDeptListByRoleId(roleId));
        tree.setDepts(deptService.buildDeptTree(list));
        return ResponseDTO.ok(tree);
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @AccessLog(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@Validated @RequestBody SysDept dept) {
        if (deptService.checkDeptNameUnique(dept.getDeptName(), null, dept.getParentId())) {
//            return Rdto.error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
            return ResponseDTO.fail();
        }

        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        SysDeptXEntity entity = new SysDeptXEntity();
        entity.setParentId(dept.getParentId());
        entity.setAncestors(dept.getAncestors());
        entity.setDeptName(dept.getDeptName());
        entity.setOrderNum(dept.getOrderNum());
        entity.setLeaderName(dept.getLeader());
        entity.setPhone(dept.getPhone());
        entity.setEmail(dept.getEmail());
        entity.setCreatorId(loginUser.getUserId());
        entity.setCreatorName(loginUser.getUsername());
        entity.insert();

        return ResponseDTO.ok();
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit') AND @ss.checkDataScopeWithDeptId(#dept.deptId)")
    @AccessLog(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysDept dept) {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (deptService.checkDeptNameUnique(dept.getDeptName(), dept.getDeptId(), dept.getParentId())) {
//            return Rdto.error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
            return ResponseDTO.fail();
        }
        if (dept.getParentId().equals(deptId)) {
//            return Rdto.error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
            return ResponseDTO.fail();
        }
        if (Objects.equals(CommonStatusEnum.DISABLE.getValue(), dept.getStatus())
            && deptService.existChildrenDeptById(deptId, true)) {
//            return Rdto.error("该部门包含未停用的子部门！");
            return ResponseDTO.fail();
        }

        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        SysDeptXEntity entity = new SysDeptXEntity();
        entity.setDeptId(deptId);
        entity.setParentId(dept.getParentId());
        entity.setAncestors(dept.getAncestors());
        entity.setDeptName(dept.getDeptName());
        entity.setOrderNum(dept.getOrderNum());
        entity.setLeaderName(dept.getLeader());
        entity.setPhone(dept.getPhone());
        entity.setEmail(dept.getEmail());
        entity.setUpdaterName(loginUser.getUsername());
        entity.setUpdaterId(loginUser.getUserId());
        entity.updateById();

        return ResponseDTO.ok();
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove') AND @ss.checkDataScopeWithDeptId(#deptId)")
    @AccessLog(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public ResponseDTO remove(@PathVariable Long deptId) {
        if (deptService.hasChildDeptById(deptId)) {
//            return Rdto.error("存在下级部门,不允许删除");
            return ResponseDTO.fail();
        }
        if (userService.checkDeptExistUser(deptId)) {
//            return Rdto.error("部门存在用户,不允许删除");
            return ResponseDTO.fail();
        }
        deptService.checkDeptDataScope(deptId);
        deptService.removeById(deptId);
        return ResponseDTO.ok();
    }
}
