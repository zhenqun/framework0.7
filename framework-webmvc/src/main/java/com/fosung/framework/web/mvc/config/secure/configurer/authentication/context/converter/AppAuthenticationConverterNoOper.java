package com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.converter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

/**
 * 默认认证对象转换器，不做任何处理
 * @Author : liupeng
 * @Date : 2018-10-20
 * @Modified By
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class AppAuthenticationConverterNoOper implements AppAuthenticationConverter {

    @Override
    public void addRegistrationId(String registrationId, ClientRegistration clientRegistration) {

    }

    @Override
    public boolean support(Authentication authentication) {
        return true ;
    }

    @Override
    public Authentication convert(Authentication authentication) {
        return authentication ;
    }
}
