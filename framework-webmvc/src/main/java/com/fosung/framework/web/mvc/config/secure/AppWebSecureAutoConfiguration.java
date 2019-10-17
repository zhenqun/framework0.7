package com.fosung.framework.web.mvc.config.secure;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.AppSecurityContextHolderStrategy;
import com.fosung.framework.web.mvc.config.secure.firewall.AppHttpFirewall;
import com.fosung.framework.web.mvc.config.secure.handler.AppAuthenticationHandlerDelegate;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * 安全配置管理
 */
@Slf4j
@Import({AppWebSecureConfiguration.class })
@Configuration
@EnableWebSecurity(debug = false)
@Order(99)
public class AppWebSecureAutoConfiguration extends WebSecurityConfigurerAdapter {

    private AppSecureProperties appSecureProperties;

    private AppAuthenticationHandlerDelegate appAuthenticationHandlerDelegate;

    private List<AppWebSecureConfigurer> appWebSecureConfigurers;

    private ApplicationContext applicationContext ;

    public AppWebSecureAutoConfiguration(AppSecureProperties appSecureProperties,
                                         ObjectProvider<AppAuthenticationHandlerDelegate> appAuthenticationHandlerDelegateObjectProvider,
                                         ObjectProvider<List<AppWebSecureConfigurer>> appWebSecureConfigurerObjectProvider ,
                                         ApplicationContext applicationContext) {
        this.appSecureProperties = appSecureProperties ;

        this.applicationContext = applicationContext ;

        this.appAuthenticationHandlerDelegate = appAuthenticationHandlerDelegateObjectProvider.getIfAvailable();

        this.appWebSecureConfigurers = appWebSecureConfigurerObjectProvider.getIfAvailable();
        if (this.appWebSecureConfigurers == null) {
            this.appWebSecureConfigurers = Lists.newArrayList();
        }else{
            for (AppWebSecureConfigurer appWebSecureConfigurer : this.appWebSecureConfigurers) {
                log.info("系统配置安全策略类: {}" , appWebSecureConfigurer.getClass().getName());
            }
        }

        // 设置session上下文的策略名称，session统一使用 AppSecurityContextImpl
        SecurityContextHolder.setStrategyName( AppSecurityContextHolderStrategy.class.getName() );

        // 初始化 AppWebSecureConfigurer 配置
        for (AppWebSecureConfigurer appWebSecureConfigurer : appWebSecureConfigurers) {
            appWebSecureConfigurer.init(appSecureProperties);
        }

    }

    @Override
    public void init(WebSecurity webSecurity) throws Exception {
        super.init(webSecurity) ;
        // 设置web防火墙
        webSecurity.httpFirewall( new AppHttpFirewall( applicationContext , appSecureProperties ) ) ;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        for (AppWebSecureConfigurer appWebSecureConfigurer : appWebSecureConfigurers) {
            appWebSecureConfigurer.configure(auth);
        }

    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 添加共享的认证处理代理对象
        httpSecurity.setSharedObject(AppAuthenticationHandlerDelegate.class, this.appAuthenticationHandlerDelegate);

        for (AppWebSecureConfigurer appWebSecureConfigurer : appWebSecureConfigurers) {
            appWebSecureConfigurer.configure(httpSecurity);
        }

        // 放开 X-Frame-Options 配置
        httpSecurity.headers().frameOptions().disable() ;

    }

}
