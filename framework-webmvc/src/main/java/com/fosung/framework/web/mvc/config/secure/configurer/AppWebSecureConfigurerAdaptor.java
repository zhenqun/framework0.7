package com.fosung.framework.web.mvc.config.secure.configurer;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.web.mvc.config.secure.AppWebSecureConfigurer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Slf4j
public abstract class AppWebSecureConfigurerAdaptor implements AppWebSecureConfigurer {

    @Setter
    @Getter
    protected AppSecureProperties appSecureProperties;

    @Override
    public void init(AppSecureProperties appSecureProperties) {
        this.appSecureProperties = appSecureProperties;
    }

    /**
     * 是否启用安全配置
     *
     */
    public boolean isEnable() {
        return appSecureProperties.getAuth().isEnable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        if( !isEnable() ){
            return ;
        }
        this.doConfigure(authenticationManagerBuilder);
    }

    public void doConfigure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        if (!isEnable()) {
            return ;
        }
        this.doConfigure(httpSecurity);
    }

    public void doConfigure(HttpSecurity httpSecurity) throws Exception {

    }


}
