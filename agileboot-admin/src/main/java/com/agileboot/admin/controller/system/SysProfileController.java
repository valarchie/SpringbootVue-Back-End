package com.agileboot.admin.controller.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.entity.SysUser;
import com.agileboot.admin.response.UserProfileDTO;
import com.agileboot.admin.response.UserProfileDTO.UserProfileDTOBuilder;
import com.agileboot.common.annotation.AccessLog;
import com.agileboot.common.config.AgileBootConfig;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.common.utils.file.FileUploadUtils;
import com.agileboot.infrastructure.web.service.TokenService;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.service.ISysUserXService;
import java.io.IOException;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 个人信息 业务处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 个人信息
     */
    @GetMapping
    public ResponseDTO profile() {
        LoginUser user = getLoginUser();

        SysUserXEntity userXEntity = userService.getById(user.getUserId());
        // TODO应该由前端处理  后端应该只返回规范的数据 而不是字符串

        UserProfileDTOBuilder profileDTO = UserProfileDTO.builder().user(new SysUser(userXEntity))
            .postGroup(userService.selectUserPostGroup(user.getUserId()))
            .roleGroup(userService.selectUserRoleGroup(user.getUserId()));

        return ResponseDTO.ok(profileDTO);
    }

    /**
     * 修改用户
     */
    @AccessLog(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO updateProfile(@RequestBody SysUser user) {
        LoginUser loginUser = getLoginUser();
        user.setUserName(loginUser.getUsername());
        if (StrUtil.isNotEmpty(user.getPhonenumber()) && userService.checkPhoneUnique(user.getPhonenumber(),
            user.getUserId())) {
//            return Rdto.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
            return ResponseDTO.fail();
        }
        if (StrUtil.isNotEmpty(user.getEmail()) && userService.checkEmailUnique(user.getEmail(), user.getUserId())) {
//            return Rdto.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
            return ResponseDTO.fail();
        }
        user.setUserId(loginUser.getUserId());
        user.setPassword(null);

        SysUserXEntity entity = user.toEntity();
        if (entity.updateById()) {
            tokenService.setLoginUser(loginUser);
            return ResponseDTO.ok();
        }
//        return Rdto.error("修改个人信息异常，请联系管理员");
        return ResponseDTO.fail();
    }

    /**
     * 重置密码
     */
    @AccessLog(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public ResponseDTO updatePwd(String oldPassword, String newPassword) {
        LoginUser loginUser = getLoginUser();
        String password = loginUser.getPassword();
        if (!AuthenticationUtils.matchesPassword(oldPassword, password)) {
//            return Rdto.error("修改密码失败，旧密码错误");
            return ResponseDTO.fail();
        }
        if (AuthenticationUtils.matchesPassword(newPassword, password)) {
//            return Rdto.error("新密码不能与旧密码相同");
            return ResponseDTO.fail();
        }
        SysUserXEntity entity = new SysUserXEntity();
        entity.setUserId(getUserId());
        entity.setPassword(AuthenticationUtils.encryptPassword(newPassword));

        if (entity.updateById()) {
            // 更新缓存用户密码
            loginUser.setPassword(AuthenticationUtils.encryptPassword(newPassword));
            tokenService.setLoginUser(loginUser);
            return ResponseDTO.ok();
        }
//        return Rdto.error("修改密码异常，请联系管理员");
        return ResponseDTO.fail();
    }

    /**
     * 头像上传
     */
    @AccessLog(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public ResponseDTO avatar(@RequestParam("avatarfile") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            LoginUser loginUser = getLoginUser();
            String avatar = FileUploadUtils.upload(AgileBootConfig.getAvatarPath(), file);

            SysUserXEntity entity = new SysUserXEntity();
            entity.setUserId(getUserId());
            entity.setAvatar(avatar);

            if (entity.updateById()) {
                // 更新缓存用户头像
                tokenService.setLoginUser(loginUser);
                return ResponseDTO.ok(new HashMap<String, String>(){{
                    put("imgUrl", avatar);
                }});
            }
        }
//        return Rdto.error("上传图片异常，请联系管理员");
        return ResponseDTO.fail();
    }
}
