package com.fosung.framework.web.mvc.config.web;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.web.common.filter.AppWebFilter;
import com.fosung.framework.web.common.filter.AppWebFilterDelegate;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.List;

@Configuration
@Slf4j
@AutoConfigureOrder( Ordered.LOWEST_PRECEDENCE )
@AutoConfigureAfter( ServletWebServerFactoryAutoConfiguration.class )
public class AppFilterAutoConfiguration {

    public List<AppWebFilter> defaultAppWebFilters(AppSecureProperties appSecureProperties){
//        AppWebCorsFilter appWebCorsFilter = new AppWebCorsFilter( appSecureProperties ) ;
//
//        AppWebRefererFilter appWebRefererFilter = new AppWebRefererFilter( appSecureProperties ) ;
//
//        AppWebXSSFilter appWebXSSFilter = new AppWebXSSFilter( appSecureProperties ) ;
//
//        return Lists.newArrayList( appWebCorsFilter , appWebRefererFilter , appWebXSSFilter ) ;

        return Lists.newArrayList() ;
    }
    @Bean
    public FilterRegistrationBean appFilterRegistration(AppSecureProperties appSecureProperties ,
                                                        ObjectProvider<List<AppWebFilter>> appWebFilterObjectProvider ){

        AppWebFilterDelegate appWebFilterDelegate = new AppWebFilterDelegate() ;

        //添加默认的过滤器
        appWebFilterDelegate.addAppWebFilters( defaultAppWebFilters( appSecureProperties ) );

        //添加应用自定义的filter
        appWebFilterDelegate.addAppWebFilters( appWebFilterObjectProvider.getIfAvailable() ) ;

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean( appWebFilterDelegate ) ;
        filterRegistrationBean.setMatchAfter( false ) ;
        //匹配所有的url
        filterRegistrationBean.addUrlPatterns( "/*" ) ;
        filterRegistrationBean.setOrder( AppProperties.FilterOrder.APP_FILTER ) ;

        log.info("添加过滤器: {}" , AppWebFilterDelegate.class.getSimpleName() ) ;

        return filterRegistrationBean ;
    }
}

