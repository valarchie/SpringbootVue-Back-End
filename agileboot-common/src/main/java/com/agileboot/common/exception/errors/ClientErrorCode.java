package com.agileboot.common.exception.errors;

/**
 * 20000~29999 客户端错误码 （客户端异常调用之类的错误）
 * @author valarchie
 */
public enum ClientErrorCode implements ErrorCodeInterface{

    /**
     * 客户端错误码
     */
    FORBIDDEN_TO_CALL(Module.COMMON, 0,"禁止调用");

    enum Module {
        /**
         * 普通模块
         */
        COMMON(0),
        /**
         * 权限模块
         */
        PERMISSION(1);

        private final int code;

        Module(int code) { this.code = code; }

        int code() { return code * 100; }
    }


    private final int code;
    private final String msg;

    private static final int BASE_CODE = 20000;

    ClientErrorCode(Module module, int code, String msg) {
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

}
