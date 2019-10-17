package com.fosung.framework.web.advice.exception;

import com.fosung.framework.web.http.ResponseParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ValidationException;

/**
 * 请求参数值或后台数据保存时数据验证的错误
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class ValidationExceptionControllerAdvice extends BaseControllerExceptionAdvice<ValidationException> {

    public ValidationExceptionControllerAdvice() {
        super(ValidationException.class.getSimpleName());
    }

    @ExceptionHandler(ValidationException.class)
    @Override
    public ResponseEntity<ResponseParam> exceptionHandler(ValidationException exception) {
        return super.exceptionHandler( exception ) ;
    }

    @Override
    public String customExceptionMsg( ValidationException exception ){
        return exception.getMessage() ;
    }

}