package com.agileboot.common.core.exception;

import com.agileboot.common.core.exception.errors.ErrorCodeInterface;
import com.agileboot.common.utils.MessageUtils;

/**
 * @author valarchie
 */
public class ApiException extends RuntimeException{

    protected ErrorCodeInterface errorCode;

    protected String message;

    protected Object[] args;

    public ApiException(Throwable e, ErrorCodeInterface errorCode, Object... args) {
        super(e);
        this.message = errorCode.message();
        this.args = args;
    }

    public ApiException(Throwable e, ErrorCodeInterface errorCode) {
        super(e);
        this.message = errorCode.message();
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCodeInterface errorCode) {
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCodeInterface errorCode, Object... args) {
        this.errorCode = errorCode;
        this.args = args;
    }

    public int getCode() { return errorCode.code(); }

    @Override
    public String getMessage() {
        return String.format(message, args);
    }

    @Override
    public String getLocalizedMessage() {
        if (errorCode != null) {
            return MessageUtils.message(errorCode.i18n(), args);
        }
        return super.getMessage();
    }
}
