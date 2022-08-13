package com.agileboot.common.exception.errors;

/**
 * 10000~19999 为内部错误码
 * @author valarchie
 */
public enum InternalErrorCode implements ErrorCodeInterface{

    /**
     * 内部错误码
     */
    INVALID_PARAMETER(Module.COMMON, 1,"参数异常"),

    UNKNOWN_ERROR(Module.COMMON, 2,"未知异常"),

    LOGIN_CAPTCHA_GENERATE_FAIL(Module.LOGIN, 1,"验证码生成失败"),

    INVALID_TOKEN(Module.PERMISSION, 1,"token异常"),

    DB_INTERNAL_ERROR(Module.DB, 1,  "数据库异常:%s"),

    ;
    enum Module {
        /**
         * 普通模块
         */
        COMMON(0),
        /**
         * 权限模块
         */
        PERMISSION(1),

        LOGIN(2),

        DB(3),

        ;

        private final int code;

        Module(int code) { this.code = code; }

        int code() { return code * 100; }
    }


    private final int code;
    private final String msg;

    private static final int BASE_CODE = 10000;

    InternalErrorCode(Module module, int code, String msg) {
        this.code = BASE_CODE + module.code() + code;
        this.msg = msg;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.msg;
    }

    @Override
    public String i18n() {
        return this.code + "_" + this.name();
    }

}
