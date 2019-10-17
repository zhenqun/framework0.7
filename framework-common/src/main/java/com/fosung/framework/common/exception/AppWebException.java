package com.fosung.framework.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * web处理过程中跑出的异常
 * @Author : liupeng
 * @Date : 2018/8/16 10:50
 * @Modified By
 */
@Getter
@Setter
public class AppWebException extends AppException {

    public AppWebException(int httpStatus , String message){
        super("" ,  httpStatus+"" , message) ;
    }

}
