package com.agileboot.common.enums;

/**
 * 用户状态
 *
 * @author ruoyi
 */
public enum LoginStatusEnum {
    /**
     * status of user
     */
    LOGIN_SUCCESS(1, "登录成功"),
    LOGOUT(1, "退出成功"),
    REGISTER(1, "注册"),
    LOGIN_FAIL(0, "登录失败");

    private final int status;
    private final String msg;

    LoginStatusEnum(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
