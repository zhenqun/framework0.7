package com.fosung.framework.web.mvc.config.web.support;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 系统映射url映射
 */
public interface AppRequestMappingInfoService {

    /**
     * 获取所有的url和方法的映射
     * @return
     */
    Map<String,HandlerMethod> getAllUrlMappings() ;

    /**
     * 获取处理当前请求的handlerMethod
     * @param request
     * @return
     */
    HandlerMethod getHandlerMethod(HttpServletRequest request) ;

    /**
     * 是否包含指定url的映射
     * @param queryUrlPattern
     * @return
     */
    boolean containUrlMapping(String queryUrlPattern) ;

}
