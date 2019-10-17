package com.fosung.framework.web.advice.exception;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.id.snowflake.AppIDGenerator;
import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.advice.AppBaseControllerAdvice;
import com.fosung.framework.web.http.ResponseParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;

/**
 * 异常检测基础类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public abstract class BaseControllerExceptionAdvice<T extends Exception> extends AppBaseControllerAdvice {

    //异常主键
    private static AppIDGenerator appIDGenerator = new AppIDGenerator();

    @Autowired
    protected AppProperties appProperties ;

    public BaseControllerExceptionAdvice(String adviceName) {
        super(adviceName);
    }

    /**
     * 异常处理
     *
     * @param exception
     * @return
     */
    public ResponseEntity<ResponseParam> exceptionHandler(T exception) {
        //获取主键
        Long exceptionId = appIDGenerator.getNextId();

        //打印日志，{主键id，建议名称，类型异常，异常信息}
        log.error("{},advice:{} , 拦截异常:{} , 异常消息:{}", exceptionId, getAdviceName(), exception.getClass().getName(),
                UtilString.isBlank(exception.getMessage()) ? "" : exception.getMessage());

        //打印异常信息栈
        if( appProperties.getException().isPrintStackTrace() ){
            StackTraceElement[] stackTraceElements = exception.getStackTrace();
            if (stackTraceElements != null) {
                for (StackTraceElement stackTraceElement : stackTraceElements) {
                    log.error("{},{}", exceptionId, stackTraceElement);
                }
            }
        }

        //获取异常提醒响应参数
        ResponseParam responseParam = getExceptionResponseParam(exception);

        //添加异常的id，便于异常跟踪
        responseParam.exceptionId( exceptionId ) ;

        log.error("{},将异常回写到响应体,{}", exceptionId, JsonMapper.toJSONString(responseParam));

        exception.printStackTrace();

        return getResponseEntity( exception , responseParam ) ;
    }

    /**
     * 根据responseParam返回ResponseEntity
     * @param exception
     * @param responseParam
     * @return
     */
    public ResponseEntity<ResponseParam> getResponseEntity( T exception , ResponseParam responseParam ){
        return responseParam.getResponseEntity() ;
    }

    /**
     * 返回异常处理的参数
     *
     * @param exception
     * @return
     */
    public ResponseParam getExceptionResponseParam(T exception) {
        ResponseParam responseParam = ResponseParam.fail();
        //标识是否为后台捕获
        responseParam.param("isCatched", true);
        //设置返回的消息内容
        responseParam.message(getExceptionMsg(exception));

        //返回错误的编码
        responseParam.code(getExceptionCode());

        return responseParam;
    }

    /**
     * 错误编码
     *
     * @return
     */
    public String getExceptionCode() {
        return "";
    }

    /**
     * 获取异常提醒的内容
     *
     * @param exception
     * @return
     */
    public String getExceptionMsg(T exception) {
        String msg = customExceptionMsg(exception);
        if (UtilString.isNotBlank(msg)) {
            return msg;
        }
        return "";
    }

    /**
     * 自定义返回的消息内容
     *
     * @param exception
     * @return
     */
    public String customExceptionMsg(T exception) {
        return exception.getMessage();
    }

}