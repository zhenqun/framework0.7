package com.fosung.framework.common.exception;

import com.fosung.framework.common.util.UtilString;
import lombok.Getter;
import lombok.Setter;

/**
 * 自定义异常类
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Getter
@Setter
public class AppException extends RuntimeException {

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 使用自定义异常
     *
     * @param message 异常信息
     */
    public AppException(String message) {
        super(message);
    }


    /**
     * 使用错误码
     *
     * @param appModeCode 模块码
     * @param errorCode   错误码
     * @param message     异常信息
     */
    public AppException(String appModeCode, String errorCode, String message) {
        super(message);
        if (UtilString.isNotBlank(appModeCode)) {
            errorCode = appModeCode + "_" + errorCode;
        }
        this.errorCode = errorCode;
    }
}
