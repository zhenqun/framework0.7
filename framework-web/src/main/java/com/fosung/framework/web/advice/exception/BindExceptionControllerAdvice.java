package com.fosung.framework.web.advice.exception;

import com.fosung.framework.web.http.ResponseParam;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;

/**
 * 绑定异常检测类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class BindExceptionControllerAdvice extends BaseControllerExceptionAdvice<BindException> {
    public BindExceptionControllerAdvice() {
        super(BindException.class.getSimpleName());
    }

    @ExceptionHandler(BindException.class)
    @Override
    public ResponseEntity<ResponseParam> exceptionHandler(BindException exception) {
        return super.exceptionHandler( exception ) ;
    }

    @Override
    public String getExceptionMsg( BindException exception ){
        BindingResult bindingResult = exception.getBindingResult() ;

        List<FieldError> fieldErrors = bindingResult.getFieldErrors() ;

        Collection<String> msg = Collections2.transform(fieldErrors, new Function<FieldError, String>() {
            @Override
            public String apply(FieldError input) {
                return input.getField()+":"+input.getDefaultMessage() ;
            }
        }) ;

        return exception.getObjectName()+" , "+ Joiner.on(";").join( msg ) ;
    }

}