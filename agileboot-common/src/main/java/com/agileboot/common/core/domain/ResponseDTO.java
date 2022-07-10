package com.agileboot.common.core.domain;

import com.agileboot.common.core.exception.errors.ErrorCode;
import com.agileboot.common.core.exception.errors.ErrorCodeInterface;
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
        return build(null, ErrorCode.SUCCESS);
    }

    public static <T> ResponseDTO<T> ok(T data) {
        return build(ErrorCode.SUCCESS, data);
    }

    public static <T> ResponseDTO<T> fail() {
        return build(null, ErrorCode.FAIL);
    }

    public static <T> ResponseDTO<T> fail(ErrorCodeInterface code) {
        return build(null, code);
    }

    public static <T> ResponseDTO<T> fail(ErrorCodeInterface code, Object... args) {
        return build( code, args);
    }

    public static <T> ResponseDTO<T> fail(T data) { return build(ErrorCode.FAIL, data); }

    public static <T> ResponseDTO<T> build(T data, ErrorCodeInterface code) {
        return new ResponseDTO<>(code.code(), code.message(), data);
    }

    public static <T> ResponseDTO<T> build(ErrorCodeInterface code, Object... args) {
        return new ResponseDTO<>(code.code(), String.format(code.message(), args), null);
    }

    public static <T> ResponseDTO<T> build(T data, ErrorCodeInterface code, Object... args) {
        return new ResponseDTO<>(code.code(), String.format(code.message(), args), data);
    }

}

