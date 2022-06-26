package com.agileboot.domain.system.user;

import com.agileboot.common.exception.ServiceException;
import com.agileboot.orm.entity.SysUserXEntity;
import java.util.List;

public class UserApplicationService {


    public String importUser(List<SysUserXEntity> userList, Boolean isUpdateSupport, String operName) {
        if (userList == null || userList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
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



}
