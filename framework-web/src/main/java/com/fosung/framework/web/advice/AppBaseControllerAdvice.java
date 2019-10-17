package com.fosung.framework.web.advice;

import com.fosung.framework.web.http.AppIBaseController;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 基础controller类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
public abstract class AppBaseControllerAdvice extends AppIBaseController {

    @Getter
    private String adviceName ;

    public AppBaseControllerAdvice(String adviceName ){
        this.adviceName = adviceName ;
    }

}