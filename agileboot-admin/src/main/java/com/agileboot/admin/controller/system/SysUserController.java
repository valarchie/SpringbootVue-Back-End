package com.agileboot.admin.controller.system;

import cn.hutool.core.collection.ListUtil;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.dto.PageDTO;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.utils.poi.CustomExcelUtil;
import com.agileboot.domain.common.BulkDeleteCommand;
import com.agileboot.domain.system.loginInfo.SearchUserQuery;
import com.agileboot.domain.system.user.UserDTO;
import com.agileboot.domain.system.user.UserDetailDTO;
import com.agileboot.domain.system.user.UserDomainService;
import com.agileboot.domain.system.user.UserInfoDTO;
import com.agileboot.domain.system.user.command.AddUserCommand;
import com.agileboot.domain.system.user.command.ChangeStatusCommand;
import com.agileboot.domain.system.user.command.ResetPasswordCommand;
import com.agileboot.domain.system.user.command.UpdateUserCommand;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.util.AuthenticationUtils;
import java.util.List;
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
    private UserDomainService userDomainService;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(SearchUserQuery query) {
        PageDTO page = userDomainService.getUserList(query);
        return ResponseDTO.ok(page);
    }

    @AccessLog(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SearchUserQuery query) {
        PageDTO userList = userDomainService.getUserList(query);
        CustomExcelUtil.writeToResponse(userList.getRows(), UserDTO.class, response);
    }

    @AccessLog(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public ResponseDTO importData(MultipartFile file, boolean updateSupport) {
        List<?> commands = CustomExcelUtil.readFromResponse(AddUserCommand.class, file);
        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        for (Object command : commands) {
            AddUserCommand addUserCommand = (AddUserCommand) command;
            userDomainService.addUser(loginUser, addUserCommand);
        }
        return ResponseDTO.ok();
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        CustomExcelUtil.writeToResponse(ListUtil.toList(new AddUserCommand()), AddUserCommand.class, response);
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public ResponseDTO<UserDetailDTO> getUserDetailInfo(@PathVariable(value = "userId", required = false) Long userId) {
//      TODO  userService.checkUserDataScope(userId);
        UserDetailDTO userDetailInfo = userDomainService.getUserDetailInfo(userId);
        return ResponseDTO.ok(userDetailInfo);
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add') AND @ss.checkDataScopeWithUserId(#command.deptId)")
    @AccessLog(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@Validated @RequestBody AddUserCommand command) {
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        userDomainService.addUser(loginUser, command);
        return ResponseDTO.ok();
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @AccessLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody UpdateUserCommand command) {
//       TODO userService.checkUserAllowed(user.getUserId());
//        userService.checkUserDataScope(user.getUserId());
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        userDomainService.updateUser(loginUser, command);
        return ResponseDTO.ok();
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @AccessLog(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public ResponseDTO remove(@PathVariable List<Long> userIds) {
        BulkDeleteCommand<Long> bulkDeleteCommand = new BulkDeleteCommand(userIds);
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        userDomainService.deleteUsers(loginUser, bulkDeleteCommand);
        return ResponseDTO.ok();
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @AccessLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{userId}/password/reset")
    public ResponseDTO resetPassword(@PathVariable Long userId, @RequestBody ResetPasswordCommand command) {
//      TODO  userService.checkUserAllowed(user.getUserId());
//        userService.checkUserDataScope(user.getUserId());
        command.setUserId(userId);
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        userDomainService.resetUserPassword(loginUser, command);
        return ResponseDTO.ok();
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @AccessLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{userId}/status")
    public ResponseDTO changeStatus(@PathVariable Long userId, @RequestBody ChangeStatusCommand command) {
//        TODO userService.checkUserAllowed(user.getUserId());
//        userService.checkUserDataScope(user.getUserId());
        command.setUserId(userId);
        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        userDomainService.changeUserStatus(loginUser, command);
        return ResponseDTO.ok();
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/{userId}/role")
    public ResponseDTO<UserInfoDTO> getRoleOfUser(@PathVariable("userId") Long userId) {
        UserInfoDTO userWithRole = userDomainService.getUserWithRole(userId);
        return ResponseDTO.ok(userWithRole);
    }



}
