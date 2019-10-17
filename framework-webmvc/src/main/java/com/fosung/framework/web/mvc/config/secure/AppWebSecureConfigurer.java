package com.fosung.framework.web.mvc.config.secure;

import com.fosung.framework.common.config.AppSecureProperties;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 应用安全配置，通过实现此类实现安全配置扩展
 * @Author : liupeng
 * @Date : 2018/8/13 15:56
 * @Modified By
 */
public interface AppWebSecureConfigurer {

    /**
     * 初始化配置
     * @param appSecureProperties
     */
    void init(AppSecureProperties appSecureProperties) ;

    /**
     * 用户认证管理配置
     * @param authenticationManagerBuilder
     */
    default void configure( AuthenticationManagerBuilder authenticationManagerBuilder ) throws Exception {

    }

    /**
     * web安全控制
     * @param httpSecurity
     */
    default void configure(HttpSecurity httpSecurity) throws Exception {

    }

}
