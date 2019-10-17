package com.fosung.framework.web.common.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 应用内部的filter统一封装类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
public abstract class AppWebFilter {

    /**
     * 不执行过滤
     * @param request
     * @return
     */
    public boolean shouldFilter(HttpServletRequest request) {
        return true ;
    }

    /**
     * 执行过滤方法
     * @param request
     * @param response
     * @return
     */
    public abstract boolean doFilterInternal(HttpServletRequest request, HttpServletResponse response ) ;

    /**
     * 对请求进行处理
     */
    public HttpServletRequest wrapRequest(HttpServletRequest httpServletRequest) {
        return httpServletRequest;
    }

    /**
     * 对请求进行处理
     */
    public HttpServletResponse wrapResponse(HttpServletResponse httpServletResponse) {
        return httpServletResponse;
    }

    /**
     * 执行完的处理
     * @param request
     * @param response
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response){

    }

}
