package com.fosung.framework.web.mvc.config.secure.configurer;

import com.fosung.framework.web.mvc.config.secure.handler.AppAuthenticationHandlerDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 退出认证策略
 */
@Slf4j
public class AppWebSecureConfigurerLogout extends AppWebSecureConfigurerAdaptor {

    @Override
    public boolean isEnable() {
        return true;
    }

    /**
     * web安全控制
     * @param httpSecurity
     */
    @Override
    public void doConfigure(HttpSecurity httpSecurity) throws Exception {
        log.info("{} 配置安全策略" , getClass().getSimpleName());

        AppAuthenticationHandlerDelegate appAuthenticationHandlerDelegate =
                httpSecurity.getSharedObject( AppAuthenticationHandlerDelegate.class ) ;

        // 退出配置
        httpSecurity.logout()
                .clearAuthentication(true)
                .logoutUrl("/logout")
                .logoutSuccessHandler( appAuthenticationHandlerDelegate )
                .permitAll() ;
    }

}
