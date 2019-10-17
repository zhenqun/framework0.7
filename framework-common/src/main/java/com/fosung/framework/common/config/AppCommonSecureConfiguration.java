package com.fosung.framework.common.config;

import com.fosung.framework.common.secure.auth.AppUserDetailsService;
import com.fosung.framework.common.secure.auth.AppUserDetailsServiceDefault;
import com.fosung.framework.common.secure.dataccess.APIDataAccessAuthService;
import com.fosung.framework.common.secure.dataccess.APIDataAccessAuthServiceDefaultImpl;
import com.fosung.framework.common.secure.dataccess.strategy.APIDataAccessAuthStrategy;
import com.fosung.framework.common.secure.dataccess.strategy.APIDataAccessAuthStrategyDefault;
import com.fosung.framework.common.secure.password.AppPasswordEncoderDefault;
import com.fosung.framework.common.secure.signature.service.SignatureKeyService;
import com.fosung.framework.common.secure.signature.service.SignatureKeyServiceDefaultImpl;
import com.fosung.framework.common.secure.signature.service.SignatureServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 添加默认的SignatureKeyService
 * @author toquery
 * @version 1
 */
@Slf4j
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
public class AppCommonSecureConfiguration {

    public AppCommonSecureConfiguration() {
//        log.info("初始化{}", this.getClass().getSimpleName());
    }

    //=====================请求签名控制======================
    /**
     * 系统没有配置签名key获取服务，则默认使用配置文件中方式获取服务
     * @return
     */
    @ConditionalOnMissingBean(SignatureKeyService.class)
    @Bean
    public SignatureKeyService signatureKeyService(){
        return new SignatureKeyServiceDefaultImpl() ;
    }

    @Bean
    public SignatureServiceImpl getSignatureService() {
        return new SignatureServiceImpl() ;
    }

    //=====================用户登录认证信息管理======================
    /**
     * 系统没有配置 AppUserDetailsService
     * @return
     */
    @ConditionalOnMissingBean(AppUserDetailsService.class)
    @Bean
    public AppUserDetailsService appUserDetailsService(){
        return new AppUserDetailsServiceDefault() ;
    }

    /**
     * 用户认证密码处理，使用应用的密码加密机制
     * @return
     */
    @ConditionalOnMissingBean(PasswordEncoder.class)
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new AppPasswordEncoderDefault() ;
    }


    //=====================数据权限控制======================

    @ConditionalOnMissingBean(APIDataAccessAuthService.class)
    @Bean
    public APIDataAccessAuthService apiDataAccessAuthService(){
        return new APIDataAccessAuthServiceDefaultImpl() ;
    }

    @ConditionalOnMissingBean(APIDataAccessAuthStrategy.class)
    @Bean
    public APIDataAccessAuthStrategy apiDataAccessAuthStrategy(){
        return new APIDataAccessAuthStrategyDefault() ;
    }



}
