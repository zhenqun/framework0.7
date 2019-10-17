package com.fosung.framework.web.mvc.config.web.support;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

@Slf4j
public class AppRequestMappingInfoServiceDefault implements AppRequestMappingInfoService {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private AntPathMatcher antPathMatcher = new AntPathMatcher( ) ;

    /**
     * 获取所有的url和方法的映射
     * @return
     */
    @Override
    public Map<String,HandlerMethod> getAllUrlMappings(){

        Map<String,HandlerMethod> urlPatternMaps = Maps.newHashMap() ;

        Map<RequestMappingInfo,HandlerMethod> requestMappingInfoHandlerMethodMap =
                requestMappingHandlerMapping.getHandlerMethods() ;

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : requestMappingInfoHandlerMethodMap.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey() ;
            HandlerMethod handlerMethod = entry.getValue() ;

            Set<String> urlPatterns = requestMappingInfo.getPatternsCondition().getPatterns() ;

            for (String urlPattern : urlPatterns) {
                urlPatternMaps.put( urlPattern , handlerMethod ) ;
                log.debug("url映射格式为: {} , 映射方法: {}" , urlPattern ,
                        handlerMethod.getMethod().getDeclaringClass().getName()+"."+handlerMethod.getMethod().getName());
            }

        }

        return urlPatternMaps ;
    }

    /**
     * 是否包含指定url的映射
     * @param queryUrlPattern
     * @return
     */
    @Override
    public boolean containUrlMapping(String queryUrlPattern){
        Assert.hasText( queryUrlPattern , "查询的url映射不能为空" ) ;

        Map<String,HandlerMethod> urlPatternMaps = getAllUrlMappings() ;

        for (String urlPattern : urlPatternMaps.keySet()) {
            if( antPathMatcher.match( urlPattern , queryUrlPattern ) ){
                log.info("系统包含url: {} 的映射" , queryUrlPattern);
                return true ;
            }
        }

        log.info("系统没有包含url: {} 的映射" , queryUrlPattern);

        return false ;
    }

    @Override
    public HandlerMethod getHandlerMethod(HttpServletRequest request) {
        try{
            HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler( request ) ;
            // 只对handlerMethod进行校验
            if( handlerExecutionChain!=null && handlerExecutionChain.getHandler() != null &&
                    handlerExecutionChain.getHandler() instanceof HandlerMethod ){
                return (HandlerMethod) handlerExecutionChain.getHandler() ;

            }
        }catch( Exception e ){
            log.error("找不到请求: {} 的处理方法" , request.getRequestURL());
        }

        return null ;
    }

}
