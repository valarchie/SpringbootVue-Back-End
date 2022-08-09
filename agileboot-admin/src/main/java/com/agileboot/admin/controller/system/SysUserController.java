package com.agileboot.admin.controller.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.domain.SysPost;
import com.agileboot.admin.deprecated.entity.SysRole;
import com.agileboot.admin.deprecated.entity.SysUser;
import com.agileboot.admin.response.UserDetailDTO;
import com.agileboot.admin.response.UserInfoDTO;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.common.utils.poi.CustomExcelUtil;
import com.agileboot.domain.system.user.UserApplicationService;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.query.system.SearchUserQuery;
import com.agileboot.orm.service.ISysPostXService;
import com.agileboot.orm.service.ISysRoleXService;
import com.agileboot.orm.service.ISysUserXService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private ISysRoleXService roleService;

    @Autowired
    private ISysPostXService postService;
    @Autowired
    private UserApplicationService userApplicationService;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public ResponseDTO<TableDataInfo> list(SearchUserQuery query) {
        Page page = getPage();
        userService.selectUserList(page, query);
        return ResponseDTO.ok(getDataTable(page));
    }

    @AccessLog(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SearchUserQuery query) {
        Page page = getPage();
        userService.selectUserList(page, query);
        List<SysUser> list = page.getRecords();

        CustomExcelUtil.writeToResponse(list, SysUser.class, response);
    }

    @AccessLog(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public ResponseDTO importData(MultipartFile file, boolean updateSupport) throws Exception {
        List<Object> objects = new ArrayList<>();

        CustomExcelUtil.readFromResponse(objects, SysUser.class, file);

        String operName = getUsername();
//        List<SysUserXEntity> collect = userList.stream().map(SysUser::toEntity).collect(Collectors.toList());

//        String message = userApplicationService.importUser(null, updateSupport, operName);
        return ResponseDTO.ok("message");
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
//        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
//        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public ResponseDTO<UserDetailDTO> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        userService.checkUserDataScope(userId);
        UserDetailDTO detailDTO = new UserDetailDTO();

        List<SysRole> roles = roleService.list().stream().map(SysRole::new).collect(Collectors.toList());

        detailDTO.setRoles(SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        detailDTO.setPosts(postService.list().stream().map(SysPost::new).collect(Collectors.toList()));

        SysUser sysUser = new SysUser(userService.getById(userId));
        detailDTO.setUser(sysUser);
        detailDTO.setPostIds(postService.selectPostListByUserId(userId));
        detailDTO.setRoleIds(sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));

        return ResponseDTO.ok(detailDTO);
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add') AND @ss.checkDataScopeWithUserId(#user.deptId)")
    @AccessLog(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@Validated @RequestBody SysUser user) {
        if (userService.checkUserNameUnique(user.getUserName())) {
//            return Rdto.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
            return ResponseDTO.fail();
        }

        if (StrUtil.isNotEmpty(user.getPhonenumber()) && userService.checkPhoneUnique(user.getPhonenumber(),
            user.getUserId())) {
//            return Rdto.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
            return ResponseDTO.fail();
        }

        if (StrUtil.isNotEmpty(user.getEmail()) && userService.checkEmailUnique(user.getEmail(), user.getUserId())) {
//            return Rdto.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
            return ResponseDTO.fail();
        }
        user.setCreateBy(getUsername());
        user.setPassword(AuthenticationUtils.encryptPassword(user.getPassword()));
        SysUserXEntity entity = user.toEntity();
        entity.insert();

        return ResponseDTO.ok();
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @AccessLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        if (StrUtil.isNotEmpty(user.getPhonenumber()) && userService.checkPhoneUnique(user.getPhonenumber(),
            user.getUserId())) {
//            return Rdto.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
            return ResponseDTO.fail();
        }
        if (StrUtil.isNotEmpty(user.getEmail()) && userService.checkEmailUnique(user.getEmail(), user.getUserId())) {
//            return Rdto.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
            return ResponseDTO.fail();
        }
        user.setUpdateBy(getUsername());
        SysUserXEntity entity = user.toEntity();
        entity.updateById();
        return ResponseDTO.ok();
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @AccessLog(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public ResponseDTO remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, getUserId())) {
//            return error("当前用户不能删除");
            return ResponseDTO.fail();
        }
        List<Long> userIdList = Arrays.stream(userIds).collect(Collectors.toList());
        userService.removeByIds(userIdList);
        return ResponseDTO.ok();
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @AccessLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public ResponseDTO resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(AuthenticationUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        SysUserXEntity entity = user.toEntity();
        entity.updateById();

        return ResponseDTO.ok();
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @AccessLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public ResponseDTO changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        SysUserXEntity entity = user.toEntity();
        entity.updateById();
        return ResponseDTO.ok();
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public ResponseDTO<UserInfoDTO> authRole(@PathVariable("userId") Long userId) {



        SysUser user = new SysUser(userService.getById(userId));
        List<SysRoleXEntity> roleXEntities = roleService.selectRolesByUserId(userId);
        List<SysRole> roles = roleXEntities.stream().map(SysRole::new).collect(Collectors.toList());

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUser(user);
        userInfoDTO.setRoles(
            SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));

        return ResponseDTO.ok(userInfoDTO);
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @AccessLog(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public ResponseDTO insertAuthRole(Long userId, Long[] roleIds) {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return ResponseDTO.ok();
    }
}
