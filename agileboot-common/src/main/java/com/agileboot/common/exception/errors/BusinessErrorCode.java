package com.agileboot.common.exception.errors;

/**
 * 40000~49999 为业务逻辑 错误码 （无异常，代码正常流转，并返回提示给用户）
 * @author valarchie
 */
public enum BusinessErrorCode implements ErrorCodeInterface{

    /**
     * 业务错误码
     */
    CAPTCHA_CODE_WRONG(Module.COMMON, 0,"操作成功");

    enum Module {
        /**
         * 普通模块
         */
        COMMON(0);

        private final int code;

        Module(int code) { this.code = code; }

        int code() { return code * 100; }
    }

    private final int code;
    private final String msg;

    private static final int BASE_CODE = 40000;

    BusinessErrorCode(Module module, int code, String msg) {
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
