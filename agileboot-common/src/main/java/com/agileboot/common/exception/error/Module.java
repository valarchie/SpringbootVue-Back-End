package com.agileboot.common.exception.error;

public enum Module {

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

    DATA_SECURITY(1),

    UPLOAD(3),

    USER(4),

    CONFIG(5),

    POST(6),

    DEPT(7),

    SYSTEM(8),

    ;


    private final int code;

    Module(int code) { this.code = code * 100; }

    public int code() {return code; }

}
