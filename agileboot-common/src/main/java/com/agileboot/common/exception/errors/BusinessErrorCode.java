package com.agileboot.common.exception.errors;

/**
 * @author valarchie
 */
public enum BusinessErrorCode {

    /**
     * 错误码集合
     * 1~9999 为保留错误码 或者常用错误码
     * 10000~19999 为内部错误码
     * 20000~29999 客户端错误码 （客户端异常调用之类的错误）
     * 30000~39999 为第三方错误码 （代码正常，但是第三方异常）
     * 40000~49999 为业务逻辑 错误码 （无异常，代码正常流转，并返回提示给用户）
     */
    // -------------- 普通错误码 及保留错误码 ---------------
    SUCCESS(0,"操作成功"),
    FAIL(9999, "操作失败"),
    FAI1L(BusinessModule.LOG.ordinal(), "操作失败"),
    FAI2L(BusinessModule.LOG, 2, "操作失败"),


    UNKNOWN_ERROR(99999,"未知错误");

    private final int code;
    private final String msg;


    BusinessErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    BusinessErrorCode(BusinessModule module, int code, String msg) {

        this.code = module.ordinal() * 100 + code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    enum BusinessModule {

        LOG,
        CAPCHAT;

    }

}
