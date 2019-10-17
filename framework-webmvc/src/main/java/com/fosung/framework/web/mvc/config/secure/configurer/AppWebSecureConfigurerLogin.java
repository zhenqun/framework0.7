package com.fosung.framework.web.mvc.config.secure.configurer;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.mvc.config.secure.configurer.authentication.details.AppAuthenticationDetailsSource;
import com.fosung.framework.web.mvc.config.secure.handler.AppAuthenticationHandlerDelegate;
import com.fosung.framework.web.mvc.config.web.support.AppRequestMappingInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.URI;

/**
 * 登录认证策略
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppWebSecureConfigurerLogin extends AppWebSecureConfigurerAdaptor {

    @Autowired(required = false)
    private UserDetailsService userDetailsService ;

    @Autowired
    private PasswordEncoder passwordEncoder ;

    @Autowired
    private AppRequestMappingInfoService requestMappingInfoService ;

    @Autowired
    private ApplicationContext applicationContext ;

    /**
     * 使用启用登录配置
     * @return
     */
    @Override
    public boolean isEnable(){
        boolean flag = getAppSecureProperties().getAuth().isEnable() && userDetailsService != null ;

        flag = flag ? UtilString.contains( appSecureProperties.getAuth().getType() ,
                AppSecureProperties.AuthConfig.LOCAL_SYSTEM ) : false ;

        log.info("{}使用本地系统用户名和密码的登录认证" , flag ? "" : "不" );

        return flag ;
    }

    @Override
    public void doConfigure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        log.info("使用 {} 作为认证密码加密器" , passwordEncoder.getClass());

        DaoAuthenticationProvider daoAuthenticationProvider = null ;
        try{
            daoAuthenticationProvider = applicationContext.getBean( DaoAuthenticationProvider.class ) ;
        }catch( Exception e ){
            log.error( "系统中没有找到自定义的 DaoAuthenticationProvider" ) ;
        }

        if( daoAuthenticationProvider==null ){
            daoAuthenticationProvider = new DaoAuthenticationProvider() ;
            daoAuthenticationProvider.setPasswordEncoder( passwordEncoder ) ;
            daoAuthenticationProvider.setUserDetailsService( userDetailsService );
        }

        authenticationManagerBuilder.authenticationProvider( daoAuthenticationProvider ) ;

        log.info("添加用户本地库的认证");
    }

    /**
     * web安全控制
     * @param httpSecurity
     */
    @Override
    public void doConfigure(HttpSecurity httpSecurity) throws Exception {
        AppSecureProperties.AuthConfig authConfig = getAppSecureProperties().getAuth() ;

        log.info("{} 配置安全策略" , getClass().getSimpleName());

        AppAuthenticationHandlerDelegate appAuthenticationHandlerDelegate =
                httpSecurity.getSharedObject( AppAuthenticationHandlerDelegate.class ) ;

        // 登录配置
        FormLoginConfigurer<HttpSecurity> formLoginConfigurer = httpSecurity.formLogin() ;

//        if( requestMappingInfoService.containUrlMapping( authConfig.getLoginUrl() ) ){
//            log.info("登录本地系统使用自定义的url映射: {}" , authConfig.getLoginUrl()) ;
//            formLoginConfigurer.loginPage( authConfig.getLoginUrl() ) ;
//        }else{
//            log.warn("项目中没有配置登录页面的访问地址, {}" , authConfig.getLoginUrl());
//        }

        log.warn("登录页面的访问地址: {} , 登录请求匹配地址: {}" , authConfig.getLoginUrl() , URI.create( authConfig.getLoginUrl() ).getPath() );
        formLoginConfigurer.loginPage( authConfig.getLoginUrl() ) ;

        // 登录请求匹配地址
        formLoginConfigurer.loginProcessingUrl( URI.create( authConfig.getLoginUrl() ).getPath() ) ;

        formLoginConfigurer.usernameParameter( authConfig.getLoginUsernameParam() )
            .passwordParameter( authConfig.getLoginPasswordParam() )
            // 设置认证源的构造参数
            .authenticationDetailsSource( new AppAuthenticationDetailsSource() )
            .successHandler( appAuthenticationHandlerDelegate )
            .failureHandler( appAuthenticationHandlerDelegate )
            .permitAll() ;

    }

}
