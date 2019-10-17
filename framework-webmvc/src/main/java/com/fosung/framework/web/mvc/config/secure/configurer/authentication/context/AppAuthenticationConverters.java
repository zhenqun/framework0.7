package com.fosung.framework.web.mvc.config.secure.configurer.authentication.context;

import com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.converter.AppAuthenticationConverter;
import com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.converter.AppAuthenticationConverterNoOper;
import com.google.common.collect.Lists;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * 转换 oauth2 存储的实体对象，可通过全局静态方法 registerAppUserDetailsDefaultConverter 注册
 * @Author : liupeng
 * @Date : 2018-10-20
 * @Modified By
 */
public class AppAuthenticationConverters {

    public static final List<AppAuthenticationConverter> APP_AUTHENTICATION_CONVERTERS  = Lists.newArrayList() ;

    static {
//        registerAppUserDetailsDefaultConverter( new AppAuthenticationConverterOAuth2() , Ordered.LOWEST_PRECEDENCE - 1 ) ;

        registerAppUserDetailsDefaultConverter( new AppAuthenticationConverterNoOper() , Ordered.LOWEST_PRECEDENCE ) ;
    }

    /**
     * 注册 appAuthenticationConverter
     * @param appAuthenticationConverter
     */
    public static void registerAppUserDetailsDefaultConverter( AppAuthenticationConverter appAuthenticationConverter , int converterOrder){
        if( appAuthenticationConverter==null || APP_AUTHENTICATION_CONVERTERS.contains(appAuthenticationConverter) ){
            return ;
        }
        APP_AUTHENTICATION_CONVERTERS.add( appAuthenticationConverter ) ;

        // 对转换器排序
        AnnotationAwareOrderComparator.sort( APP_AUTHENTICATION_CONVERTERS ) ;
    }

    /**
     * 转换认证对象，找到第一个支持认证对象转换器 并且 转换结果不为空的转换器
     * @param authentication
     * @return
     */
    public static Authentication convert( Authentication authentication ){

        for (AppAuthenticationConverter appAuthenticationConverter : APP_AUTHENTICATION_CONVERTERS) {
            if( appAuthenticationConverter.support( authentication ) ){
                Authentication convertedAuthentication =  appAuthenticationConverter.convert( authentication );
                if( convertedAuthentication != null ){
                    return convertedAuthentication ;
                }
            }
        }

        return null ;
    }

}
