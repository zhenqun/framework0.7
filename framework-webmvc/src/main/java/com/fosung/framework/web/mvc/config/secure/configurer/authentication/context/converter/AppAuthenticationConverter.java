package com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.converter;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

/**
 * 认证对象转换器，将系统生成的认证对象转换为其他类型
 * @Author : liupeng
 * @Date : 2018-10-20
 * @Modified By
 */
public interface AppAuthenticationConverter {

    /**
     * 添加注册客户端的id
     * @param registrationId
     * @param clientRegistration 客户端注册信息
     */
    void addRegistrationId(String registrationId , ClientRegistration clientRegistration) ;

    /**
     * 是否支持转换
     * @param authentication
     * @return
     */
    <S extends Authentication> boolean support(S authentication) ;

    /**
     * 执行转换
     * @param authentication
     * @return
     */
    <T extends Authentication,S extends Authentication> T convert(T authentication ) ;



}
