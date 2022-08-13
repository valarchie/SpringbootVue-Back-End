package com.agileboot.admin.controller.system;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.domain.SysUserRole;
import com.agileboot.admin.deprecated.entity.SysRole;
import com.agileboot.admin.deprecated.entity.SysUser;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.domain.system.role.RoleApplicationService;
import com.agileboot.domain.system.role.RoleModel;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.service.TokenService;
import com.agileboot.infrastructure.web.service.UserDetailsServiceImpl;
import com.agileboot.infrastructure.web.util.AuthenticationUtils;
import com.agileboot.orm.entity.SysRoleXEntity;
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
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private RoleApplicationService roleApplicationService;

    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public ResponseDTO<TableDataInfo> list(SysRole role) {

        Page<SysRoleXEntity> page = getPage();
        QueryWrapper<SysRoleXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(role.getStatus() != null, "status", role.getStatus())
            .eq(role.getRoleKey() != null, "role_key", role.getRoleKey())
            .like(StrUtil.isNotEmpty(role.getRoleName()), "role_name", role.getRoleName());

        roleService.page(page, queryWrapper);
        return ResponseDTO.ok(getDataTable(page));
    }

    @AccessLog(title = "角色管理", businessType = BusinessType.EXPORT)
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
//        ExcelUtil<SysRole> util = new ExcelUtil<>(SysRole.class);
//        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public ResponseDTO getInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(roleId);
        return ResponseDTO.ok(new SysRole(roleService.getById(roleId)));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @AccessLog(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@Validated @RequestBody SysRole role) {
        if (roleService.checkRoleNameUnique(role.getRoleId(), role.getRoleName())) {
//            return Rdto.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
            return ResponseDTO.fail();
        }
        if (roleService.checkRoleKeyUnique(role.getRoleId(), role.getRoleKey())) {
//            return Rdto.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
            return ResponseDTO.fail();
        }
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        role.setCreateBy(loginUser.getUsername());

        RoleModel roleModel = role.toModel();
        roleApplicationService.createRole(roleModel);

        return ResponseDTO.ok();

    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role.getRoleId());
        roleService.checkRoleDataScope(role.getRoleId());
        if (roleService.checkRoleNameUnique(role.getRoleId(), role.getRoleName())) {
//            return Rdto.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
            return ResponseDTO.fail();
        }
        if (roleService.checkRoleKeyUnique(role.getRoleId(), role.getRoleKey())) {
//            return Rdto.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
            return ResponseDTO.fail();
        }
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        role.setUpdateBy(loginUser.getUsername());

        RoleModel roleModel = role.toModel();

        if (roleApplicationService.updateRole(roleModel)) {
            // 更新缓存用户权限
            if (loginUser != null && AuthenticationUtils.isAdmin(loginUser.getUserId())) {
                loginUser.setMenuPermissions(userDetailsService.getMenuPermissions(loginUser.getUserId()));
                tokenService.setLoginUser(loginUser);
            }
            return ResponseDTO.ok();
        }
//        return Rdto.error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
        return ResponseDTO.fail();
    }

    /**
     * 修改保存数据权限
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{roleId}/dataScope")
    public ResponseDTO dataScope(@PathVariable("roleId")Long roleId, @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role.getRoleId());
        roleService.checkRoleDataScope(role.getRoleId());
        RoleModel roleModel = role.toModel();
        roleApplicationService.authDataScope(roleModel);

        return ResponseDTO.ok();
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{roleId}/status")
    public ResponseDTO changeStatus(@PathVariable("roleId")Long roleId, @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role.getRoleId());
        roleService.checkRoleDataScope(role.getRoleId());
        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        role.setUpdateBy(loginUser.getUsername());

        SysRoleXEntity roleEntity = roleService.getById(role.getRoleId());
        roleEntity.setStatus(Convert.toInt(role.getStatus()));
        roleEntity.updateById();

        return ResponseDTO.ok();
    }

    /**
     * 没找到 调用的地方
     * 删除角色
     */
//    @PreAuthorize("@ss.hasPermi('system:role:remove')")
//    @AccessLog(title = "角色管理", businessType = BusinessType.DELETE)
//    @DeleteMapping("/{roleIds}")
//    public ResponseDTO remove(@PathVariable Long[] roleIds) {
//        List<Long> idList = Arrays.stream(roleIds).collect(Collectors.toList());
//        roleService.removeByIds(idList);
//        return ResponseDTO.ok();
//    }

    /**
     * 没在前端找到调用的地方
     * 获取角色选择框列表
     */
//    @PreAuthorize("@ss.hasPermi('system:role:query')")
//    @GetMapping("/optionselect")
//    public ResponseDTO optionselect() {
//        List<SysRole> collect = roleService.list().stream().map(SysRole::new).collect(Collectors.toList());
//        return ResponseDTO.ok(collect);
//    }

    /**
     * 查询已分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/{roleId}/allocated/list")
    public ResponseDTO<TableDataInfo> allocatedList(@PathVariable("roleId")Long roleId, SysUser user) {
        Page page = getPage();
        userService.selectAllocatedList(user.getRoleId(), user.getUserName(), user.getPhonenumber(), page);
        return ResponseDTO.ok(getDataTable(page));
    }

    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/{roleId}/unallocated/list")
    public ResponseDTO<TableDataInfo> unallocatedList(@PathVariable("roleId")Long roleId, SysUser user) {
        Page page = getPage();
        userService.selectUnallocatedList(user.getRoleId(), user.getUserName(), user.getPhonenumber(), page);
        return ResponseDTO.ok(getDataTable(page));
    }

    /**
     * 取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessType.GRANT)
    @DeleteMapping("/{roleId}/user/grant")
    public ResponseDTO cancelAuthUser(@PathVariable("roleId")Long roleId, @RequestBody SysUserRole userRole) {
        roleApplicationService.deleteAuthUser(userRole.getRoleId(), userRole.getUserId());
        return ResponseDTO.ok();
    }

    /**
     * 批量取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessType.GRANT)
    @DeleteMapping("/{roleId}/user/grant/bulk")
    public ResponseDTO cancelAuthUserAll(@PathVariable("roleId")Long roleId, Long[] userIds) {
        List<Long> userIdList = Arrays.stream(userIds).collect(Collectors.toList());
        roleApplicationService.deleteAuthUsers(roleId, userIdList);

        return ResponseDTO.ok();
    }

    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @AccessLog(title = "角色管理", businessType = BusinessType.GRANT)
    @PostMapping("/{roleId}/user/grant/bulk")
    public ResponseDTO selectAuthUserAll(@PathVariable("roleId") Long roleId, Long[] userIds) {
        List<Long> userIdList = Arrays.stream(userIds).collect(Collectors.toList());
        roleService.checkRoleDataScope(roleId);
        roleApplicationService.insertAuthUsers(roleId, userIdList);
        return ResponseDTO.ok();
    }
}
