package com.fosung.framework.web.mvc.config.web;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.web.advice.AppRequestParamValidateAdvice;
import com.fosung.framework.web.advice.exception.*;
import com.fosung.framework.web.converter.AppMessageConverter;
import com.fosung.framework.web.mvc.config.web.configurer.AppResponseParamConfigurer;
import com.fosung.framework.web.mvc.config.web.configurer.AppWebMvcConfigurer;
import com.fosung.framework.web.mvc.config.web.support.AppRequestMappingInfoService;
import com.fosung.framework.web.mvc.config.web.support.AppRequestMappingInfoServiceDefault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.List;

@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AppMvcAutoConfiguration {

    private MultipartProperties multipartProperties;

    private AppProperties appProperties ;

    public AppMvcAutoConfiguration(MultipartProperties multipartProperties , AppProperties appProperties) {
        log.info("自定义或扩展springmvc的配置类");

        this.multipartProperties = multipartProperties ;
        this.appProperties = appProperties ;
    }

    @Bean
    public AppWebMvcConfigurer appWebMvcConfigurer(ObjectProvider<List<AppMessageConverter>> appMessageConverterProvider ){
        AppWebMvcConfigurer appWebMvcConfigurer = new AppWebMvcConfigurer() ;
        appWebMvcConfigurer.setAppMessageConverters( appMessageConverterProvider.getIfAvailable() ) ;

        return appWebMvcConfigurer ;
    }

    @Bean
    public AppResponseParamConfigurer responseParamConfigurer(){
        return new AppResponseParamConfigurer( appProperties ) ;
    }

    /**
     * 自定义文件上传类，修复文件大小不能限制的问题
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public CommonsMultipartResolver multipartResolver(){

        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver() ;

        commonsMultipartResolver.setDefaultEncoding("UTF-8") ;

//        commonsMultipartResolver.setMaxUploadSize( FileSize.valueOf(multipartProperties.getMaxRequestSize()).getSize());
//
//        commonsMultipartResolver.setMaxUploadSizePerFile( FileSize.valueOf(multipartProperties.getMaxFileSize()).getSize());

        log.info("mvc文件上传处理类: {}" , commonsMultipartResolver.getClass().getName());

        return commonsMultipartResolver ;
    }

    /**
     * 系统参数映射信息
     * @return
     */
    @Bean
    public AppRequestMappingInfoService appRequestMappingInfoService(){
        return new AppRequestMappingInfoServiceDefault() ;
    }

    //=======================系统异常处理拦截类，start========================
    @Bean
    public ExceptionControllerAdvice exceptionControllerAdvice(){
        return new ExceptionControllerAdvice() ;
    }

    @Bean
    public AppExceptionControllerAdvice appExceptionControllerAdvice(){
        return new AppExceptionControllerAdvice() ;
    }

    @Bean
    public BindExceptionControllerAdvice bindExceptionControllerAdvice(){
        return new BindExceptionControllerAdvice() ;
    }

    @Bean
    public MethodArgumentNotValidExceptionControllerAdvice methodArgumentNotValidExceptionControllerAdvice(){
        return new MethodArgumentNotValidExceptionControllerAdvice() ;
    }

    @Bean
    public ValidationExceptionControllerAdvice validationExceptionControllerAdvice(){
        return new ValidationExceptionControllerAdvice() ;
    }

    /**
     * 请求方法拦截advice类，验证请求参数中 @Valid 注解
     * @return
     */
    @Bean
    public AppRequestParamValidateAdvice requestParamValidateAdvice(){
        return new AppRequestParamValidateAdvice() ;
    }

    //=======================系统异常处理拦截类，end========================

}
