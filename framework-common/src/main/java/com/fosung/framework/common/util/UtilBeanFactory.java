package com.fosung.framework.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 运行时spring对象管理类
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
@Component
public class UtilBeanFactory implements ApplicationContextAware {

    private static ApplicationContext APPLICATION_CONTEXT  = null ;

    @Override
    public void setApplicationContext( ApplicationContext applicationContext ){
        log.info("设置公共的应用上下文");
        APPLICATION_CONTEXT = applicationContext ;
    }

    /**
     * 返回公共的请求上下文对象
     * @return
     */
    public static ApplicationContext getApplicationContext(){
//        Assert.notNull( APPLICATION_CONTEXT , "应用上下文对象没有初始化" );
        return APPLICATION_CONTEXT ;
    }

}
