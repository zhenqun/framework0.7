package com.fosung.framework.web.mvc.config.secure.configurer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 跨域认证策略
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppWebSecureConfigurerCSRF extends AppWebSecureConfigurerAdaptor {

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
        log.info("{} 配置安全策略, 禁用spring security 自带的csrf" , getClass().getSimpleName());
        //禁用csrf
        httpSecurity.csrf().disable() ;
    }

}
