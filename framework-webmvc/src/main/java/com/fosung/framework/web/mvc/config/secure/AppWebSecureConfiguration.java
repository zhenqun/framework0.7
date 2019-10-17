package com.fosung.framework.web.mvc.config.secure;

import com.fosung.framework.web.mvc.config.secure.configurer.*;
import com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.converter.AppAuthenticationConverterOAuth2DTDefault;
import com.fosung.framework.web.mvc.config.secure.cookie.AppCookieJSCallback;
import com.fosung.framework.web.mvc.config.secure.cookie.AppCookieTokenCallback;
import com.fosung.framework.web.mvc.config.secure.handler.*;
import com.fosung.framework.web.mvc.config.secure.oauth2.AppOAuth2ClientRegistrationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.List;

@Configuration
@Slf4j
public class AppWebSecureConfiguration {

    /**
     * 系统默认url访问策略
     * @return
     */
    @Bean
    public AppWebSecureConfigurerAnonDefault appWebSecureConfigurerAnonDefault(){
        return new AppWebSecureConfigurerAnonDefault() ;
    }

    /**
     * 基于oauth2客户端注册配置的资源库
     * @return
     */
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    @Bean
    public AppOAuth2ClientRegistrationRepository oAuth2ClientRegistrationRepository(){
        log.info("使用自定义的ClientRegistrationRepository: "+ AppOAuth2ClientRegistrationRepository.class.getSimpleName() );
        return new AppOAuth2ClientRegistrationRepository() ;
    }

    /**
     * 添加对灯塔用户转换处理的类
     * @return
     */
    @Bean
    public AppAuthenticationConverterOAuth2DTDefault appAuthenticationConverterOAuth2DTDefault(){
        return new AppAuthenticationConverterOAuth2DTDefault() ;
    }

    /**
     * 认证成功的默认处理
     * @return
     */
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandlerDefault(){
        return new AppAuthenticationSuccessHandlerDefault() ;
    }

    /**
     * 认证失败的默认处理
     * @return
     */
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandlerDefault(){
        return new AppAuthenticationFailureHandlerDefault() ;
    }

    /**
     * 退出成功的默认处理
     * @return
     */
    @ConditionalOnMissingBean(LogoutSuccessHandler.class)
    @Bean
    public LogoutSuccessHandler logoutSuccessHandlerDefault(){
        return new AppLogoutSuccessHandlerDefault() ;
    }

    /**
     * 请求访问无权限处理
     * @return
     */
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new AppAccessDeniedHandlerDefault() ;
    }

//    /**
//     * 用户认证密码处理，使用应用的密码加密机制
//     * @return
//     */
//    @ConditionalOnMissingBean(PasswordEncoder.class)
//    @Bean
//    public PasswordEncoder appPasswordEncoderDefault(){
//        return new AppPasswordEncoderDefault() ;
//    }

    @Bean
    public AppWebSecureConfigurerCSRF appWebSecureConfigurerCSRF(){
        return new AppWebSecureConfigurerCSRF() ;
    }

    @Bean
    public AppWebSecureConfigurerLogin appWebSecureConfigurerLogin(){
        return new AppWebSecureConfigurerLogin() ;
    }

    @Bean
    public AppWebSecureConfigurerSSOClient appWebSecureConfigurerSSOClient(){
        return new AppWebSecureConfigurerSSOClient() ;
    }

    @Bean
    public AppWebSecureConfigurerLogout appWebSecureConfigurerLogout(){
        return new AppWebSecureConfigurerLogout() ;
    }

    @Bean
    public AppWebSecureConfigurerUrl appWebSecureConfigurerUrl(){
        return new AppWebSecureConfigurerUrl() ;
    }

    /**
     * 访问拒绝的处理
     * @return
     */
    @Bean
    public AppWebSecureConfigurerAccessDenied appWebSecureConfigurerAccessDenied(){
        return new AppWebSecureConfigurerAccessDenied() ;
    }

    /**
     * session访问策略
     * @return
     */
    @Bean
    public AppWebSecureConfigurerSessionStrategy appWebSecureConfigurerSessionStrategy(){
        return new AppWebSecureConfigurerSessionStrategy() ;
    }

    @Bean
    public AppAuthenticationHandlerDelegate appAuthenticationHandlerDelegate(
            ObjectProvider<List<AuthenticationSuccessHandler>> authenticationSuccessHandlersProvider ,
            ObjectProvider<List<AuthenticationFailureHandler>> authenticationFailureHandlersProvider ,
            ObjectProvider<List<LogoutSuccessHandler>> logoutSuccessHandlersProvider ,
            ObjectProvider<List<AccessDeniedHandler>> accessDeniedHandlersProvider
    ){

        AppAuthenticationHandlerDelegate appAuthenticationHandlerDelegate = new AppAuthenticationHandlerDelegate() ;

        // 认证成功处理
        appAuthenticationHandlerDelegate.setAuthenticationSuccessHandlers( authenticationSuccessHandlersProvider.getIfAvailable() ) ;

        // 认证失败处理
        appAuthenticationHandlerDelegate.setAuthenticationFailureHandlers( authenticationFailureHandlersProvider.getIfAvailable() ) ;

        // 登出成功处理
        appAuthenticationHandlerDelegate.setLogoutSuccessHandlers( logoutSuccessHandlersProvider.getIfAvailable() ) ;

        // 访问拒绝处理
        appAuthenticationHandlerDelegate.setAccessDeniedHandlers( accessDeniedHandlersProvider.getIfAvailable() ) ;

        return appAuthenticationHandlerDelegate ;
    }

    @Bean
    public AppCookieJSCallback appCookieJSCallback(){
        log.info("初始化cookie的跨域回调，支持跨域cookie返回");
        return new AppCookieJSCallback() ;
    }

    @Bean
    public AppCookieTokenCallback appCookieTokenCallback(){
        log.info("初始化cookie的跨域回调，支持跨域cookie token返回");
        return new AppCookieTokenCallback() ;
    }

}
