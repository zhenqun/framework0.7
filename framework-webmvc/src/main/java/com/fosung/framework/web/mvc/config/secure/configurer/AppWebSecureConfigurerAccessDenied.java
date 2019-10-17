package com.fosung.framework.web.mvc.config.secure.configurer;

import com.fosung.framework.web.mvc.config.secure.handler.AppAuthenticationHandlerDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 请求访问被拒绝的处理
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppWebSecureConfigurerAccessDenied extends AppWebSecureConfigurerAdaptor {

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public void doConfigure(HttpSecurity httpSecurity) throws Exception {

        log.info("{} 配置安全策略" , getClass().getSimpleName());

        AppAuthenticationHandlerDelegate appAuthenticationHandlerDelegate =
                httpSecurity.getSharedObject( AppAuthenticationHandlerDelegate.class ) ;

        httpSecurity.exceptionHandling().accessDeniedHandler( appAuthenticationHandlerDelegate ) ;
    }

}
