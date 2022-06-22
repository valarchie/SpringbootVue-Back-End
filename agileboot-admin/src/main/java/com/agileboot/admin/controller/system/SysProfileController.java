package com.agileboot.admin.controller.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.common.annotation.Log;
import com.agileboot.common.config.RuoYiConfig;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.utils.file.FileUploadUtils;
import com.agileboot.infrastructure.loginuser.AuthenticationUtils;
import com.agileboot.infrastructure.loginuser.LoginUser;
import com.agileboot.infrastructure.web.service.TokenService;
import com.springvue.orm.domain.entity.SysUser;
import com.springvue.orm.domain.test.sys.po.SysUserXEntity;
import com.springvue.orm.domain.test.sys.service.ISysUserXService;
import java.io.IOException;
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
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        ResponseDTO ajax = ResponseDTO.success(user);
        // TODO应该由前端处理  后端应该只返回规范的数据 而不是字符串
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        ajax.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        return ajax;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO updateProfile(@RequestBody SysUser user) {
        LoginUser loginUser = getLoginUser();
        SysUser sysUser = loginUser.getUser();
        user.setUserName(sysUser.getUserName());
        if (StrUtil.isNotEmpty(user.getPhonenumber()) && userService.checkPhoneUnique(user)) {
            return ResponseDTO.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (StrUtil.isNotEmpty(user.getEmail()) && userService.checkEmailUnique(user)) {
            return ResponseDTO.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUserId(sysUser.getUserId());
        user.setPassword(null);

        SysUserXEntity entity = user.toEntity();
        if (entity.updateById()) {
            // 更新缓存用户信息
            sysUser.setNickName(user.getNickName());
            sysUser.setPhonenumber(user.getPhonenumber());
            sysUser.setEmail(user.getEmail());
            sysUser.setSex(user.getSex());
            tokenService.setLoginUser(loginUser);
            return ResponseDTO.success();
        }
        return ResponseDTO.error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public ResponseDTO updatePwd(String oldPassword, String newPassword) {
        LoginUser loginUser = getLoginUser();
        String password = loginUser.getPassword();
        if (!AuthenticationUtils.matchesPassword(oldPassword, password)) {
            return ResponseDTO.error("修改密码失败，旧密码错误");
        }
        if (AuthenticationUtils.matchesPassword(newPassword, password)) {
            return ResponseDTO.error("新密码不能与旧密码相同");
        }
        SysUserXEntity entity = new SysUserXEntity();
        entity.setUserId(getUserId());
        entity.setPassword(AuthenticationUtils.encryptPassword(newPassword));

        if (entity.updateById()) {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(AuthenticationUtils.encryptPassword(newPassword));
            tokenService.setLoginUser(loginUser);
            return ResponseDTO.success();
        }
        return ResponseDTO.error("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public ResponseDTO avatar(@RequestParam("avatarfile") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            LoginUser loginUser = getLoginUser();
            String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file);

            SysUserXEntity entity = new SysUserXEntity();
            entity.setUserId(getUserId());
            entity.setAvatar(avatar);

            if (entity.updateById()) {
                ResponseDTO ajax = ResponseDTO.success();
                ajax.put("imgUrl", avatar);
                // 更新缓存用户头像
                loginUser.getUser().setAvatar(avatar);
                tokenService.setLoginUser(loginUser);
                return ajax;
            }
        }
        return ResponseDTO.error("上传图片异常，请联系管理员");
    }
}
