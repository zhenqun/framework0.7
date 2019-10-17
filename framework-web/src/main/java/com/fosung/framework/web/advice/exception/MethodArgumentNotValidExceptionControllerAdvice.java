package com.fosung.framework.web.advice.exception;

import com.fosung.framework.web.http.ResponseParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 方法参数异常检测
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class MethodArgumentNotValidExceptionControllerAdvice extends BaseControllerExceptionAdvice<MethodArgumentNotValidException> {
    public MethodArgumentNotValidExceptionControllerAdvice() {
        super(MethodArgumentNotValidException.class.getSimpleName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Override
    public ResponseEntity<ResponseParam> exceptionHandler(MethodArgumentNotValidException exception) {
        log.error("{}", "MethodArgumentNotValidException");
        return super.exceptionHandler( exception ) ;
    }

    @Override
    public String getExceptionMsg( MethodArgumentNotValidException exception ){
        String defaultMessage = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return defaultMessage;
    }
}
