package com.fosung.framework.web.mvc.config.secure.configurer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * app.auth.enable=false启用此配置，所有请求都将授权
 * app.auth.enable=true关闭此配置，根据配置去授权，具体配置见 AppWebSecureConfigurerUrl
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppWebSecureConfigurerDisableAuth extends AppWebSecureConfigurerAdaptor {

    @Override
    public boolean isEnable() {
        return !super.isEnable();
    }

    /**
     * web安全控制
     * @param httpSecurity
     */
    @Override
    public void doConfigure(HttpSecurity httpSecurity) throws Exception {
        log.info("{} 配置安全策略" , getClass().getSimpleName());
        httpSecurity.authorizeRequests().antMatchers("/**").permitAll() ;
    }

}
