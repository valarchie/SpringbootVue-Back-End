package com.agileboot.domain.system.user;

import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.service.ISysConfigXService;
import com.agileboot.orm.service.ISysUserXService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService {

    @Autowired
    private ISysUserXService userService;

    @Autowired
    private ISysConfigXService configService;

//    @Autowired
//    private RedisCache redisCache;


    public String importUser(List<SysUserXEntity> userList, Boolean isUpdateSupport, String operName) {

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
