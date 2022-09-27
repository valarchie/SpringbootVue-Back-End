package com.agileboot.common.exception.error;

public interface ErrorCodeInterface {

    int code();

    String message();

    default String i18n() {
        return code() + "_" + message();
    }

}
