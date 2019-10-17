package com.fosung.framework.web.mvc.config.secure.oauth2;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.AppAuthenticationConverters;
import com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.converter.AppAuthenticationConverter;
import com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.converter.AppAuthenticationConverterOAuth2;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.ClassUtils;

import java.util.Map;

@Slf4j
public class AppOAuth2ClientRegistrationRepository implements ClientRegistrationRepository , InitializingBean {

    @Autowired
    private AppSecureProperties appSecureProperties ;

    @Autowired
    private ApplicationContext applicationContext ;

    private Map<String,ClientRegistration> clientRegistrations = Maps.newHashMap() ;

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {

        return clientRegistrations.get( registrationId ) ;
    }

    /**
     * 根据注册id查询oauth2配置
     * @param registrationId
     * @return
     */
    public AppSecureProperties.OAuth2Config findOAuth2ConfigByRegistrationId(String registrationId) {
        if( appSecureProperties.getSso().getOauth2Configs()==null ||
                appSecureProperties.getSso().getOauth2Configs().size()<1 ){
            log.warn("没有配置oauth2认证信息");
            return null ;
        }

        for (AppSecureProperties.OAuth2Config oauth2Config : appSecureProperties.getSso().getOauth2Configs()) {
            if( UtilString.equalsIgnoreCase( oauth2Config.getRegistrationId() , registrationId ) ){
                return oauth2Config ;
            }
        }

        return null ;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if( appSecureProperties.getSso().getOauth2Configs()==null ||
                appSecureProperties.getSso().getOauth2Configs().size()<1 ){
            log.warn("没有配置oauth2认证信息");
            return ;
        }

        for (AppSecureProperties.OAuth2Config oauth2Config : appSecureProperties.getSso().getOauth2Configs()) {
            ClientRegistration clientRegistration = ClientRegistration.withRegistrationId( oauth2Config.getRegistrationId() )
                    .clientId( oauth2Config.getClientId() )
                    .clientSecret( oauth2Config.getClientSecret() )
                    .clientName( oauth2Config.getClientName() )
                    .redirectUriTemplate( oauth2Config.getRedirectUriTemplate() )
                    .clientAuthenticationMethod( new ClientAuthenticationMethod( oauth2Config.getClientAuthenticationMethod() ) )
                    .authorizationGrantType( new AuthorizationGrantType( oauth2Config.getAuthorizationGrantType() ) )
                    .authorizationUri( oauth2Config.getAuthorizationUri() )
                    .tokenUri( oauth2Config.getTokenUri() )
                    .userInfoUri( oauth2Config.getUserInfoUri() )
                    .userNameAttributeName( oauth2Config.getUserNameAttributeName() )
                    .scope( UtilString.split(oauth2Config.getScope(),";") )
                    .build() ;

            AppAuthenticationConverter appAuthenticationConverter = null ;

            if( UtilString.isBlank( oauth2Config.getUserDetailsConverter() ) ){
                appAuthenticationConverter = new AppAuthenticationConverterOAuth2() ;
            }else{
                Class<?> userDetailsConverterClass = ClassUtils.forName( oauth2Config.getUserDetailsConverter() , ClassUtils.getDefaultClassLoader() ) ;
                appAuthenticationConverter = (AppAuthenticationConverter)applicationContext.getBean( userDetailsConverterClass ) ;
            }
            //添加转换的注册对象id
            appAuthenticationConverter.addRegistrationId( oauth2Config.getRegistrationId() , clientRegistration) ;

            log.info("oauth2注册id: {} 转换器为: {}" , oauth2Config.getRegistrationId() , appAuthenticationConverter.getClass().getName() ) ;

            AppAuthenticationConverters.registerAppUserDetailsDefaultConverter( appAuthenticationConverter , 100 ) ;

            clientRegistrations.put( clientRegistration.getRegistrationId() , clientRegistration ) ;
        }

    }
}
