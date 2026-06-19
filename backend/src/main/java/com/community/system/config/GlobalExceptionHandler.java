package com.community.system.config;

import com.community.system.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统兜底异常拦截: ", e);
        return Result.error(500, "系统开小差了，请稍后再试");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.error(400, message);
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingParamException(MissingServletRequestParameterException e) {
        return Result.error(400, "缺少必填参数: " + e.getParameterName());
    }
}
