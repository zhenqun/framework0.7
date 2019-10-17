package com.fosung.framework.web.mvc.config.secure.configurer.authentication.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * 格式化请求的上下文，保存存储的对象都是 AppSecurityContextImpl
 * @Author : liupeng
 * @Date : 2018-10-20
 * @Modified By
 */
@Slf4j
public class AppSecurityContextImpl extends SecurityContextImpl {

    @Override
    public void setAuthentication(Authentication authentication) {
        super.setAuthentication( AppAuthenticationConverters.convert( authentication ) );
    }
}
