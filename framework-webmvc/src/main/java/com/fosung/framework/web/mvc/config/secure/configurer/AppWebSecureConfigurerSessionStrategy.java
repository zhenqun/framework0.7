package com.fosung.framework.web.mvc.config.secure.configurer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * session管理策略
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppWebSecureConfigurerSessionStrategy extends AppWebSecureConfigurerAdaptor {

    private SessionCreationPolicy sessionCreationPolicy = SessionCreationPolicy.ALWAYS ;

    @Override
    public boolean isEnable() {
        return true ;
    }

    @Override
    public void doConfigure(HttpSecurity httpSecurity) throws Exception {
        log.info("配置session创建策略: {}" , sessionCreationPolicy);
        // 配置session加载策略，影响 SecurityContextPersistenceFilter 中的session加载策略
        httpSecurity.sessionManagement().sessionCreationPolicy( sessionCreationPolicy ) ;
    }

}
