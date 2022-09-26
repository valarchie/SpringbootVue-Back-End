package com.agileboot.domain.system.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import com.agileboot.common.core.dto.PageDTO;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.domain.common.BulkDeleteCommand;
import com.agileboot.domain.system.loginInfo.SearchUserQuery;
import com.agileboot.domain.system.post.PostDTO;
import com.agileboot.domain.system.role.RoleDTO;
import com.agileboot.domain.system.user.UserProfileDTO.UserProfileDTOBuilder;
import com.agileboot.domain.system.user.command.AddUserCommand;
import com.agileboot.domain.system.user.command.ChangeStatusCommand;
import com.agileboot.domain.system.user.command.ResetPasswordCommand;
import com.agileboot.domain.system.user.command.UpdateProfileCommand;
import com.agileboot.domain.system.user.command.UpdateUserAvatarCommand;
import com.agileboot.domain.system.user.command.UpdateUserCommand;
import com.agileboot.domain.system.user.command.UpdateUserPasswordCommand;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.service.TokenService;
import com.agileboot.orm.entity.SysRoleEntity;
import com.agileboot.orm.entity.SysUserEntity;
import com.agileboot.orm.result.SearchUserDO;
import com.agileboot.orm.service.ISysConfigService;
import com.agileboot.orm.service.ISysDeptService;
import com.agileboot.orm.service.ISysPostService;
import com.agileboot.orm.service.ISysRoleService;
import com.agileboot.orm.service.ISysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDomainService {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;


    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private TokenService tokenService;



    public PageDTO getUserList(SearchUserQuery query) {
        Page<SearchUserDO> searchUserDOPage = userService.selectUserList(query);
        List<UserDTO> userDTOList = searchUserDOPage.getRecords().stream().map(UserDTO::new).collect(Collectors.toList());
        return new PageDTO(userDTOList, searchUserDOPage.getTotal());
    }

    public UserProfileDTO getUserProfile(Long userId) {

        SysUserEntity userXEntity = userService.getById(userId);
        // TODO应该由前端处理  后端应该只返回规范的数据 而不是字符串

        UserProfileDTOBuilder profileDTO = UserProfileDTO.builder().user(new UserDTO(userXEntity))
            .postGroup(userService.selectUserPostGroup(userId))
            .roleGroup(userService.selectUserRoleGroup(userId));

        return profileDTO.build();
    }


    public void updateUserProfile(UpdateProfileCommand command) {
        UserModel userModel = getUserModel(command.getUserId());
        command.updateModel(userModel);

        userModel.checkPhoneNumberIsUnique(userService);
        userModel.checkEmailIsUnique(userService);

        userModel.updateById();
    }

    public UserDetailDTO getUserDetailInfo(Long userId) {
        SysUserEntity userEntity = userService.getById(userId);
        UserDetailDTO detailDTO = new UserDetailDTO();

        List<RoleDTO> roleDTOs = roleService.list().stream().map(RoleDTO::new).collect(Collectors.toList());
        List<PostDTO> postDTOs = postService.list().stream().map(PostDTO::new).collect(Collectors.toList());
        detailDTO.setRoles(roleDTOs);
        detailDTO.setPosts(postDTOs);

        if (userEntity != null) {
            detailDTO.setUser(new UserDTO(userEntity));
            detailDTO.setRoleId(userEntity.getRoleId());
            detailDTO.setPostId(userEntity.getPostId());
        }
        return detailDTO;
    }

    public void addUser(LoginUser loginUser, AddUserCommand command) {
        UserModel model = command.toModel();

        model.checkUsernameIsUnique(userService);
        model.checkPhoneNumberIsUnique(userService);
        model.checkEmailIsUnique(userService);

        model.setCreatorId(loginUser.getUserId());
        model.setCreatorName(loginUser.getUsername());

        model.insert();
    }

    public void updateUser(LoginUser loginUser, UpdateUserCommand command) {
        UserModel model = command.toModel();

//        model.checkUsernameIsUnique(userService);
        model.checkPhoneNumberIsUnique(userService);
        model.checkEmailIsUnique(userService);

        model.setUpdaterId(loginUser.getUserId());
        model.setUpdaterName(loginUser.getUsername());

        model.updateById();
    }

    @Transactional
    public void deleteUsers(LoginUser loginUser, BulkDeleteCommand<Long> command) {
        for (Long id : command.getIds()) {
            UserModel userModel = getUserModel(id);
            userModel.checkCanBeDelete(loginUser);
            userModel.deleteById();
        }
    }

    public void updateUserPassword(LoginUser loginUser, UpdateUserPasswordCommand command) {
        UserModel userModel = getUserModel(command.getUserId());
        userModel.modifyPassword(command);
        userModel.updateById();

        loginUser.setPassword(userModel.getPassword());
        tokenService.setLoginUser(loginUser);
    }

    public void resetUserPassword(LoginUser loginUser, ResetPasswordCommand command) {
        UserModel userModel = getUserModel(command.getUserId());
        userModel.setPassword(command.getPassword());

        userModel.setUpdaterId(loginUser.getUserId());
        userModel.setUpdaterName(loginUser.getUsername());
        userModel.updateById();
    }

    public void changeUserStatus(LoginUser loginUser, ChangeStatusCommand command) {
        UserModel userModel = getUserModel(command.getUserId());
        userModel.setStatus(Convert.toInt(command.getStatus()));

        userModel.setUpdaterId(loginUser.getUserId());
        userModel.setUpdaterName(loginUser.getUsername());
        userModel.updateById();
    }

    public void updateUserAvatar(LoginUser loginUser, UpdateUserAvatarCommand command) {
        UserModel userModel = getUserModel(command.getUserId());
        userModel.setAvatar(command.getAvatar());
        userModel.updateById();
        tokenService.setLoginUser(loginUser);
    }

    public UserInfoDTO getUserWithRole(Long userId) {
        UserModel userModel = getUserModel(userId);
        UserDTO userDTO = new UserDTO(userModel);

        SysRoleEntity roleEntity = roleService.getById(userModel.getRoleId());
        RoleDTO roleDTO = new RoleDTO(roleEntity);

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUser(userDTO);
        userInfoDTO.setRoles(ListUtil.of(roleDTO));
        return userInfoDTO;
    }

    public UserModel getUserModel(Long userId) {
        SysUserEntity byId = userService.getById(userId);
        if (byId == null) {
            throw new ApiException(BusinessErrorCode.OBJECT_NOT_FOUND, userId, "用户");
        }

        UserModel userModel = new UserModel();
        BeanUtil.copyProperties(byId, userModel);
        return userModel;
    }


    public String importUser(List<SysUserEntity> userList, Boolean isUpdateSupport, String operName) {

        if (1 == 1) {
            return "jackson";
        }

        if (userList == null || userList.size() == 0) {
            throw new ApiException(BusinessErrorCode.USER_IMPORT_DATA_IS_NULL);
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
//        String password = configService.getConfigValueByKey("sys.user.initPassword");
//        for (SysUser user : userList) {
//            try {
//                // 验证是否存在这个用户
//                SysUser u = this.getUserByUserName(user.getUserName());
//                if (u == null) {
//                    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(user);
//                    if (!constraintViolations.isEmpty()) {
//                        throw new ConstraintViolationException(constraintViolations);
//                    }
//                    user.setPassword(AuthenticationUtils.encryptPassword(password));
//                    user.setCreateBy(operName);
//                    SysUserXEntity entity = user.toEntity();
//                    entity.insert();
//
//                    successNum++;
//                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
//                } else if (isUpdateSupport) {
//                    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(user);
//                    if (!constraintViolations.isEmpty()) {
//                        throw new ConstraintViolationException(constraintViolations);
//                    }
//                    user.setUpdateBy(operName);
//                    SysUserXEntity entity = user.toEntity();
//
//                    entity.updateById();
//
//                    successNum++;
//                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName())
//                        .append(" 更新成功");
//                } else {
//                    failureNum++;
//                    failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName())
//                        .append(" 已存在");
//                }
//            } catch (Exception e) {
//                failureNum++;
//                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
//                failureMsg.append(msg + e.getMessage());
//                log.error(msg, e);
//            }
//        }
//        if (failureNum > 0) {
//            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
//            throw new ServiceException(failureMsg.toString());
//        } else {
//            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
//        }
        return successMsg.toString();
    }



    /**
     * 注册
     */
//    public String register(RegisterUserModel registerModel) {
//        String msg = "", username = registerModel.getUsername(), password = registerModel.getPassword();
//
//        boolean captchaOnOff = configService.isCaptchaOn();
//        // 验证码开关
//        if (captchaOnOff) {
//            validateCaptcha(username, registerModel.getCode(), registerModel.getUuid());
//        }
//
//        if (StrUtil.isEmpty(username)) {
//            msg = "用户名不能为空";
//        } else if (StrUtil.isEmpty(password)) {
//            msg = "用户密码不能为空";
//        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
//            || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
//            msg = "账户长度必须在2到20个字符之间";
//        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
//            || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
//            msg = "密码长度必须在5到20个字符之间";
//        } else if (userService.checkUserNameUnique(username)) {
//            msg = "保存用户'" + username + "'失败，注册账号已存在";
//        } else {
//            SysUserXEntity entity = new SysUserXEntity();
//
//            entity.setUsername(username);
//            entity.setPassword(AuthenticationUtils.encryptPassword(registerModel.getPassword()));
//
//            boolean regFlag = entity.insert();
//            if (!regFlag) {
//                msg = "注册失败,请联系系统管理人员";
//            } else {
//                AsyncManager.me().execute(AsyncFactory.recordLoginInfo(username, Constants.REGISTER,
//                    MessageUtils.message("user.register.success")));
//            }
//        }
//        return msg;
//    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
//    public void validateCaptcha(String username, String code, String uuid) {
//        String verifyKey = Constants.CAPTCHA_CODE_KEY + StrUtil.emptyIfNull(uuid);
//        String captcha = redisCache.getCacheObject(verifyKey);
//        redisCache.deleteObject(verifyKey);
//        if (captcha == null) {
//            throw new ApiException(BusinessErrorCode.CAPTCHA_CODE_NULL);
//        }
//        if (!code.equalsIgnoreCase(captcha)) {
//            throw new ApiException(BusinessErrorCode.CAPTCHA_CODE_WRONG);
//        }
//    }



}
