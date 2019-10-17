package com.fosung.framework.web.advice.exception;


import com.fosung.framework.common.exception.AppException;
import com.fosung.framework.common.exception.AppWebException;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.http.ResponseParam;
import com.google.common.primitives.Ints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 自定义异常处理类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@ControllerAdvice
public class AppExceptionControllerAdvice extends BaseControllerExceptionAdvice<AppException> {

    public AppExceptionControllerAdvice() {
        super(AppException.class.getSimpleName());
    }

    /**
     * 自定义异常处理类
     *
     * @param exception
     * @return
     */
    @Override
    @ExceptionHandler(AppException.class)
    @ResponseBody
    public ResponseEntity<ResponseParam> exceptionHandler(AppException exception) {

        return super.exceptionHandler(exception);
    }

    @Override
    public ResponseEntity<ResponseParam> getResponseEntity(AppException exception , ResponseParam responseParam) {
        //如果web跑出的异常，则来带http状态码一起返回
        if( exception instanceof AppWebException && UtilString.isNotBlank( exception.getErrorCode() )){
            return responseParam.getResponseEntity( HttpStatus.valueOf(Ints.tryParse( exception.getErrorCode() )) ) ;
        }

        return super.getResponseEntity(exception , responseParam);
    }

    /**
     * 返回异常处理的参数
     *
     * @param exception
     * @return
     */
    @Override
    public ResponseParam getExceptionResponseParam(AppException exception) {
        ResponseParam responseParam = ResponseParam.fail();

        if (!UtilString.isEmpty(exception.getErrorCode())) {
            responseParam.code(exception.getErrorCode());
        }
        responseParam.message(exception.getMessage());

        //标识是否为后台捕获
        responseParam.param("isCatched", true);

        return responseParam;
    }


}
