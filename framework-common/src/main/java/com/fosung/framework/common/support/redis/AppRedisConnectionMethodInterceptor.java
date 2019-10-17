package com.fosung.framework.common.support.redis ;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Set;

/**
 * redis连接拦截器
 * @Author : liupeng
 * @Date : 2019-03-27
 * @Modified By
 */
@Slf4j
public class AppRedisConnectionMethodInterceptor implements MethodInterceptor {

    /**
     * 不支持的redis方法
     */
    public static final Set<String> UN_SUPPORT_METHODS = Sets.newHashSet(
            "setConfig" , "getConfig" , "configGet" ,
            "rename" , "renameNX" ,
            "bLPop" , "bRPop" , "bRPopLPush" , "getSubscription" ,
            "publish" , "subscribe" , "pSubscribe" ) ;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 过滤不支持的redis方法
        if( UN_SUPPORT_METHODS.contains( invocation.getMethod().getName() ) ){
            log.info("redis方法不支持:{}" , invocation.getMethod().getName() ) ;
            return null ;
        }

        return invocation.proceed() ;
    }
}
