package com.fosung.framework.web.advice.exception;


import com.fosung.framework.web.http.ResponseParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Exception异常检测类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
@ResponseBody
public class ExceptionControllerAdvice extends BaseControllerExceptionAdvice<Exception> {

    public ExceptionControllerAdvice() {
        super(Exception.class.getSimpleName());
    }

    @ExceptionHandler(Exception.class)
    @Override
    public ResponseEntity<ResponseParam> exceptionHandler(Exception exception) {
        return super.exceptionHandler( exception ) ;
    }

    @Override
    public String getExceptionCode(){
        return "exception" ;
    }
}
