package com.fosung.framework.web.mvc.config.secure.configurer;

import com.fosung.framework.common.util.UtilString;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.Set;

/**
 * 默认不需要添加权限的url
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppWebSecureConfigurerAnonDefault extends AppWebSecureConfigurerAdaptor {

    private Set<String> anonUrlPatterns = Sets.newHashSet("/logout" ,"/error" ,
            "/api/dictionary", "/dictionary" ,
            "/ueditor/**" , "/api/global/cookie-js.do" , "/api/global/cookie-token.do" ) ;

    @Override
    public boolean isEnable() {
        return true ;
    }

    @Override
    public void doConfigure(HttpSecurity httpSecurity) throws Exception {

        log.info("不需要权限校验的url: {}" , UtilString.joinByComma( anonUrlPatterns )) ;

        httpSecurity.authorizeRequests()
                .antMatchers( anonUrlPatterns.toArray( new String[]{} ) ).permitAll() ;

    }

}
