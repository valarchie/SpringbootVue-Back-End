package com.agileboot.common.core.domain;

import com.agileboot.common.exception.errors.ErrorCode;
import com.agileboot.common.exception.errors.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 响应信息主体
 *
 * @author valarchie
 */
@Data
@AllArgsConstructor
public class ResponseDTO<T> {

    private Integer code;

    private String msg;

    private T data;

    public static <T> ResponseDTO<T> ok() {
        return build(ErrorCode.SUCCESS, null);
    }

    public static <T> ResponseDTO<T> ok(T data) {
        return build(ErrorCode.SUCCESS, data);
    }

    public static <T> ResponseDTO<T> ok(T data, String msg) {
        return build(ErrorCode.SUCCESS, data, msg);
    }

    public static <T> ResponseDTO<T> fail() {
        return build(ErrorCode.FAIL, null);
    }

    public static <T> ResponseDTO<T> fail(String msg) {
        return build(ErrorCode.FAIL, null, msg);
    }

    public static <T> ResponseDTO<T> fail(ErrorCode code) {
        return build(code, null);
    }

    public static <T> ResponseDTO<T> fail(T data) {
        return build(ErrorCode.FAIL, data);
    }

    public static <T> ResponseDTO<T> fail(T data, String msg) {
        return build(ErrorCode.FAIL, data, msg);
    }


    public static <T> ResponseDTO<T> build(ErrorCodeInterface code, T data) {
        return new ResponseDTO<>(code.code(), code.message(), data);
    }

    public static <T> ResponseDTO<T> build(ErrorCodeInterface code, T data, String msg) {
        return new ResponseDTO<>(code.code(), msg, data);
    }
}

