package com.agileboot.domain.system.user;

import cn.hutool.core.util.StrUtil;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.domain.system.user.command.UpdateUserPasswordCommand;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.util.AuthenticationUtils;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.service.ISysUserXService;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserModel extends SysUserXEntity {

    public void checkUsernameIsUnique(ISysUserXService userService) {
        if (userService.checkUserNameUnique(getUsername())) {
            throw new ApiException(BusinessErrorCode.USER_NAME_IS_NOT_UNIQUE);
        }
    }


    public void checkPhoneNumberIsUnique(ISysUserXService userService) {
        if (StrUtil.isNotEmpty(getPhoneNumber()) && userService.checkPhoneUnique(getPhoneNumber(),
            getUserId())) {
            throw new ApiException(BusinessErrorCode.USER_PHONE_NUMBER_IS_NOT_UNIQUE);
        }
    }

    public void checkEmailIsUnique(ISysUserXService userService) {
        if (StrUtil.isNotEmpty(getEmail()) && userService.checkEmailUnique(getEmail(), getUserId())) {
            throw new ApiException(BusinessErrorCode.USER_EMAIL_IS_NOT_UNIQUE);
        }
    }

    public void checkCanBeDelete(LoginUser loginUser) {
        if (Objects.equals(getUserId(), loginUser.getUserId())) {
            throw new ApiException(BusinessErrorCode.USER_CURRENT_USER_CAN_NOT_BE_DELETE);
        }
    }


    public void modifyPassword(UpdateUserPasswordCommand command) {
        if (!AuthenticationUtils.matchesPassword(command.getOldPassword(), getPassword())) {
            throw new ApiException(BusinessErrorCode.USER_PASSWORD_IS_NOT_CORRECT);
        }

        if (AuthenticationUtils.matchesPassword(command.getNewPassword(), getPassword())) {
            throw new ApiException(BusinessErrorCode.USER_NEW_PASSWORD_IS_THE_SAME_AS_OLD);
        }
        setPassword(AuthenticationUtils.encryptPassword(command.getNewPassword()));
    }



}
