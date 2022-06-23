package com.agileboot.admin.controller.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.common.annotation.Log;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.common.utils.poi.ExcelUtil;
import com.agileboot.infrastructure.web.service.SysPermissionService;
import com.agileboot.infrastructure.web.service.TokenService;
import com.agileboot.orm.deprecated.domain.SysUserRole;
import com.agileboot.orm.deprecated.entity.SysRole;
import com.agileboot.orm.deprecated.entity.SysUser;
import com.agileboot.orm.po.SysRoleXEntity;
import com.agileboot.orm.service.ISysRoleXService;
import com.agileboot.orm.service.ISysUserXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
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
 * 角色信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {

    @Autowired
    private ISysRoleXService roleService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysUserXService userService;

    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRole role) {

        Page<SysRoleXEntity> page = getPage();
        QueryWrapper<SysRoleXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(role.getStatus() != null, "status", role.getStatus())
            .eq(role.getRoleKey() != null, "role_key", role.getRoleKey())
            .like(StrUtil.isNotEmpty(role.getRoleName()), "role_name", role.getRoleName());

        roleService.page(page, queryWrapper);
        return getDataTable(page);
    }

    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRole role) {

        Page<SysRoleXEntity> page = getPage();
        QueryWrapper<SysRoleXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(role.getStatus() != null, "status", role.getStatus())
            .eq(role.getRoleKey() != null, "role_key", role.getRoleKey())
            .like(StrUtil.isNotEmpty(role.getRoleName()), "role_name", role.getRoleName());

        roleService.page(page, queryWrapper);

        List<SysRole> list = page.getRecords().stream().map(SysRole::new).collect(Collectors.toList());
        ExcelUtil<SysRole> util = new ExcelUtil<>(SysRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public ResponseDTO getInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(roleId);
        return ResponseDTO.success(new SysRole(roleService.getById(roleId)));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@Validated @RequestBody SysRole role) {
        if (roleService.checkRoleNameUnique(role)) {
            return ResponseDTO.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (roleService.checkRoleKeyUnique(role)) {
            return ResponseDTO.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(getUsername());
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (roleService.checkRoleNameUnique(role)) {
            return ResponseDTO.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (roleService.checkRoleKeyUnique(role)) {
            return ResponseDTO.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(getUsername());

        if (roleService.updateRole(role)) {
            // 更新缓存用户权限
            LoginUser loginUser = getLoginUser();
            if (loginUser != null && AuthenticationUtils.isAdmin(loginUser.getUserId())) {
                loginUser.setMenuPermissions(permissionService.getMenuPermission(loginUser.getUserId()));
                userService.selectUserByUserName(loginUser.getUsername());
                tokenService.setLoginUser(loginUser);
            }
            return ResponseDTO.success();
        }
        return ResponseDTO.error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 修改保存数据权限
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public ResponseDTO dataScope(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public ResponseDTO changeStatus(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public ResponseDTO remove(@PathVariable Long[] roleIds) {
        List<Long> idList = Arrays.stream(roleIds).collect(Collectors.toList());
        return toAjax(roleService.removeByIds(idList));
    }

    /**
     * 获取角色选择框列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public ResponseDTO optionselect() {
        return ResponseDTO.success(roleService.list().stream().map(SysRole::new).collect(Collectors.toList()));
    }

    /**
     * 查询已分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo allocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectAllocatedList(user);
        return getDataTable(list);
    }

    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo unallocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUnallocatedList(user);
        return getDataTable(list);
    }

    /**
     * 取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public ResponseDTO cancelAuthUser(@RequestBody SysUserRole userRole) {
        return toAjax(roleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public ResponseDTO cancelAuthUserAll(Long roleId, Long[] userIds) {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public ResponseDTO selectAuthUserAll(Long roleId, Long[] userIds) {
        roleService.checkRoleDataScope(roleId);
        return toAjax(roleService.insertAuthUsers(roleId, userIds));
    }
}
