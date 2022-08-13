package com.agileboot.common.exception.errors;

/**
 * 30000~39999 为第三方错误码 （代码正常，但是第三方异常）
 * @author valarchie
 */
public enum ExternalErrorCode implements ErrorCodeInterface{

    /**
     * 支付宝调用失败
     */
    FAIL_TO_PAY_ON_ALIPAY(Module.COMMON, 1,"支付宝调用失败");


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

        int code() {return code * 100; }
    }


    private final int code;
    private final String msg;

    private static final int BASE_CODE = 30000;

    ExternalErrorCode(Module module, int code, String msg) {
        this.code = BASE_CODE + module.code() + code;
        this.msg = msg;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String message() { return this.msg; }

    @Override
    public String i18n() {
        return this.code + "_" + this.name();
    }

}
