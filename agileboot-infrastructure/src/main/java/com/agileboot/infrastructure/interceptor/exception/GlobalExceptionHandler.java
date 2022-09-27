package com.agileboot.infrastructure.interceptor.exception;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.agileboot.common.core.dto.Rdto;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.error.ErrorCode;
import com.google.common.util.concurrent.UncheckedExecutionException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author valarchie TODO 需要整改
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Rdto handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return Rdto.error(HttpStatus.HTTP_FORBIDDEN, "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Rdto handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
        HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return Rdto.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ApiException.class)
    public Rdto handleServiceException(ApiException e, HttpServletRequest request) {
        if (e.getCode() == ErrorCode.Internal.DB_INTERNAL_ERROR.code()) {
            String sensitiveInfoBegin = "### The error may exist";
            String nonSensitiveMsg = StrUtil.subBefore(e.getMessage(), sensitiveInfoBegin, false);
            return Rdto.error(e.getCode(), nonSensitiveMsg);
        }
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return code != null ? Rdto.error(code, e.getMessage()) : Rdto.error(e.getMessage());
    }

    /**
     * 捕获缓存类当中的错误
     */
    @ExceptionHandler(UncheckedExecutionException.class)
    public ResponseDTO handleServiceException(UncheckedExecutionException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        return ResponseDTO.fail(e.getMessage());
    }


    /**
     * 捕获DB层当中的错误
     */
//    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseDTO handleServiceException(BadSqlGrammarException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        // TODO 抛出数据库错误
        return ResponseDTO.fail(e.getMessage());
    }


    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Rdto handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return Rdto.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Rdto handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return Rdto.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public Rdto handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return Rdto.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Rdto.error(message);
    }


}
