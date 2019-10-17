package com.fosung.framework.web.mvc.config.secure.configurer;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.web.mvc.config.secure.configurer.support.OAuth2LoginRequestURLFilter;
import com.fosung.framework.web.mvc.config.secure.handler.AppAuthenticationHandlerDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;

/**
 * sso 登录认证策略
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppWebSecureConfigurerSSOClient extends AppWebSecureConfigurerAdaptor {

    @Override
    public boolean isEnable() {
        AppSecureProperties.SSOConfig ssoConfig = getAppSecureProperties().getSso() ;
        return ssoConfig.isEnable() ;
    }

    @Override
    public void doConfigure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        super.doConfigure(authenticationManagerBuilder);
    }

    /**
     * web安全控制
     * @param httpSecurity
     */
    @Override
    public void doConfigure(HttpSecurity httpSecurity) throws Exception {
        AppSecureProperties.SSOConfig ssoConfig = getAppSecureProperties().getSso() ;

        log.info("启用基于 oauth2 sso 配置");

        // 允许以 /oauth2 开始的 登录请求
        httpSecurity.authorizeRequests().antMatchers("/oauth2/**").permitAll() ;

        AppAuthenticationHandlerDelegate appAuthenticationHandlerDelegate =
                httpSecurity.getSharedObject( AppAuthenticationHandlerDelegate.class ) ;

        httpSecurity.oauth2Login()
                // 认证拦截跳转拦截的url前缀
                .authorizationEndpoint().baseUri( ssoConfig.getLoginUrl() )
                .and()
                // oauth登录拦截的地址
                .loginProcessingUrl( ssoConfig.getLoginUrl() )
                .successHandler( appAuthenticationHandlerDelegate )
                .failureHandler( appAuthenticationHandlerDelegate ) ;

        // 处理回调redirecturl校验的bug
        // bug校验位置： OAuth2LoginAuthenticationProvider 的115行，INVALID_REDIRECT_URI_PARAMETER_ERROR_CODE
        httpSecurity.addFilterBefore( new OAuth2LoginRequestURLFilter( getAppSecureProperties() ) ,
                OAuth2LoginAuthenticationFilter.class ) ;

    }



}
