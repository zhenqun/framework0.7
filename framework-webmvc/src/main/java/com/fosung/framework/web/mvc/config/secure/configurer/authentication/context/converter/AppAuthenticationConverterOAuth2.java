package com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.converter;

import com.alibaba.fastjson.JSON;
import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.secure.auth.AppUserDetailsDefault;
import com.fosung.framework.common.secure.auth.support.UserAuth;
import com.fosung.framework.common.secure.auth.support.UserOrg;
import com.fosung.framework.common.util.UtilAuthentication;
import com.fosung.framework.common.util.UtilBeanFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

import java.util.*;

/**
 * oauth2 认证对象转换器，默认将 oauth2认证对象转换成 UsernamePasswordAuthenticationToken
 * @Author : liupeng
 * @Date : 2018-10-20
 * @Modified By
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AppAuthenticationConverterOAuth2 implements AppAuthenticationConverter {

    @Getter
    private Set<String> registrationIds = Sets.newHashSet() ;

    @Getter
    private Map<String,ClientRegistration> clientIdRegistrations = Maps.newHashMap() ;

    public AppAuthenticationConverterOAuth2(){
    }

    public AppAuthenticationConverterOAuth2(String registrationId){
        this.registrationIds.add( registrationId ) ;
        Assert.hasText( registrationId , "客户端注册id不能为空" );
    }

    /**
     * 添加客户端注册id
     * @param registrationId
     */
    @Override
    public void addRegistrationId(String registrationId , ClientRegistration clientRegistration){
        this.registrationIds.add( registrationId ) ;
        this.clientIdRegistrations.put( clientRegistration.getClientId() , clientRegistration ) ;
    }

    @Override
    public boolean support(Authentication authentication) {
        if( ! ( authentication instanceof OAuth2AuthenticationToken ) ){
            return false ;
        }
        OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken)authentication ;

        return this.registrationIds.contains( oauth2AuthenticationToken.getAuthorizedClientRegistrationId() ) ;
    }

    @Override
    public Authentication convert(Authentication authentication) {
        OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken)authentication ;

        OAuth2User oauth2User = oauth2AuthenticationToken.getPrincipal() ;

        log.info("用户对象："+JsonMapper.toJSONString( oauth2User )) ;

        AppUserDetailsDefault appUserDetailsDefault = new AppUserDetailsDefault( oauth2User.getName() ) ;
        // 设置登录用户来源为oauth2
        appUserDetailsDefault.setAuthSource("oauth2") ;
        // 设置用户id
        appUserDetailsDefault.setUserId( loadUserId( oauth2User ) ) ;
        // 设置用户真实姓名
        appUserDetailsDefault.setUserRealName( loadUserRealName( oauth2User ) );
        // 设置用户手机号
        appUserDetailsDefault.setTelephone( loadTelephone( oauth2User ) );
        // 装载用户授权
        appUserDetailsDefault.getAuthorities().addAll( loadUserAuthorities( oauth2User , appUserDetailsDefault ) ) ;
        // 装载默认授权
        appUserDetailsDefault.getAuthorities().addAll( loadDefaultUserAuthorities() ) ;
        // 设置用户其它属性
        appUserDetailsDefault.setUserProperties( oauth2User.getAttributes() ) ;
        // 添加客户端注册id属性
        appUserDetailsDefault.setClientRegistrationId( oauth2AuthenticationToken.getAuthorizedClientRegistrationId() );
//        appUserDetailsDefault.addUserProperty( "clientRegistrationId" , oauth2AuthenticationToken.getAuthorizedClientRegistrationId() ) ;

        // 执行其他的转换
        convertMore( appUserDetailsDefault  , oauth2User ) ;

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null ;

        // 认证通过的用户添加授权信息；没有认证通过的用户不添加授权信息
        if( authentication.isAuthenticated() ){
            usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken( appUserDetailsDefault , null ,
                            appUserDetailsDefault.getAuthorities() ) ;
        }else{
            usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken( appUserDetailsDefault , null ) ;
        }

        return usernamePasswordAuthenticationToken ;
    }

    /**
     * 装载用户id
     * @param oauth2User
     * @return
     */
    public Object loadUserId( OAuth2User oauth2User ){
        List<Object> userIds = Lists.newArrayList( oauth2User.getAttributes().get("id") ,
                oauth2User.getAttributes().get("userId")  , oauth2User.getAttributes().get("user_id") ) ;

        Optional<Object> userIdOptional = userIds.stream().filter( item-> item!=null ).findFirst() ;

        if( !userIdOptional.isPresent() ){
            log.error("在返回的用户对象中找不到用户id. {}" , JsonMapper.toJSONString( oauth2User.getAttributes() ) ) ;
        }

        return userIdOptional.isPresent() ? userIdOptional.get() : null ;
    }

    /**
     * 装载用户真实姓名
     * @param oauth2User
     * @return
     */
    public String loadUserRealName( OAuth2User oauth2User ){
        List<Object> userIds = Lists.newArrayList( oauth2User.getAttributes().get("name") ,
                oauth2User.getAttributes().get("alias") ,  oauth2User.getAttributes().get("realname") ,
                oauth2User.getAttributes().get("username") ) ;

        Optional<String> userNameOptional = userIds.stream().filter( item-> item!=null ).map(item->item.toString()).findFirst() ;

        if( !userNameOptional.isPresent() ){
            log.error("在返回的用户对象中找不到用户名称. {}" , JsonMapper.toJSONString( oauth2User.getAttributes() ) ) ;
        }

        return userNameOptional.isPresent() ? userNameOptional.get() : null ;
    }

    /**
     * 装载用户手机号
     * @param oauth2User
     * @return
     */
    public String loadTelephone( OAuth2User oauth2User ){
        List<Object> telephones = Lists.newArrayList( oauth2User.getAttributes().get("telephone") ,
                oauth2User.getAttributes().get("mobile") ) ;

        Optional<String> telephoneOptional = telephones.stream().filter( item-> item!=null ).map(item->item.toString()).findFirst() ;

        if( !telephoneOptional.isPresent() ){
            log.error("在返回的用户对象中找不到手机号. {}" , JsonMapper.toJSONString( oauth2User.getAttributes() ) ) ;
        }

        return telephoneOptional.isPresent() ? telephoneOptional.get() : null ;
    }

    /**
     * 转载用户权限
     * @param oauth2User
     * @return
     */
    public Collection<GrantedAuthority> loadUserAuthorities(OAuth2User oauth2User , AppUserDetailsDefault appUserDetailsDefault){

        List<Object> userRoleAttributes = Lists.newArrayList( oauth2User.getAttributes().get("role") ,
                oauth2User.getAttributes().get("roles") , oauth2User.getAttributes().get("authorities") ) ;

        // 定义用户授权列表
        List<GrantedAuthority> grantedAuthorities = Lists.newArrayList() ;

        Set<String> userRoleCodes = Sets.newHashSet() ;

        for (Object userRoleAttribute : userRoleAttributes) {
            if( userRoleAttribute == null ){
                continue;
            }
            if( userRoleAttribute instanceof Collection ){
                Collection userRoleAttributeCollection = (Collection)userRoleAttribute ;
                for (Object role : userRoleAttributeCollection) {
                    if( role==null ){
                        continue;
                    }
                    userRoleCodes.add( role.toString().trim() ) ;
                }
            }else{
                userRoleCodes.add( userRoleAttribute.toString().trim() ) ;
            }
        }

        log.info("oauth获取用户角色列表：{}" , JsonMapper.toJSONString( userRoleCodes )) ;

        // 创建授权对象
        grantedAuthorities.addAll( UtilAuthentication.convertRoleToGrantedAuthority( userRoleCodes ) ) ;

        return grantedAuthorities ;
    }

    /**
     * 转载默认的授权角色
     * @return
     */
    public Collection<GrantedAuthority> loadDefaultUserAuthorities(){
        // 定义用户授权列表
        List<GrantedAuthority> grantedAuthorities = Lists.newArrayList() ;

        // 添加系统默认的角色信息
        AppSecureProperties appSecureProperties = UtilBeanFactory.getApplicationContext().getBean(AppSecureProperties.class) ;

        grantedAuthorities.addAll( UtilAuthentication.convertRoleToGrantedAuthority(
                appSecureProperties.getAuth().getDefaultUserRoles() ) ) ;

        return grantedAuthorities ;
    }

    /**
     * 执行其它的属性转换
     * @param appUserDetailsDefault
     * @param oAuth2User
     */
    public void convertMore( AppUserDetailsDefault appUserDetailsDefault , OAuth2User oAuth2User ){
        // 解析用户权限
        try{
            Object authsObject = oAuth2User.getAttributes().get("auths") ;
            if( authsObject!=null ){
                List<UserAuth> userAuths = JSON.parseArray( JSON.toJSONString( authsObject ) , UserAuth.class ) ;
                appUserDetailsDefault.setUserAuths( userAuths ) ;
            }
        }catch( Exception e ){
            log.error( "解析用户权限异常 : {}" , e.getMessage() ) ;
        }

        // 解析用户所属组织机构
        try{
            Object userOrgObject = oAuth2User.getAttributes().get("orgs") ;
            if( userOrgObject!=null ){
                List<UserOrg> userOrgs = JSON.parseArray( JSON.toJSONString( userOrgObject ) , UserOrg.class ) ;
                appUserDetailsDefault.setUserOrgs( userOrgs ) ;
            }
        }catch( Exception e ){
            log.error( "解析所属组织机构异常 : {}" , e.getMessage() ) ;
        }
    }

}
