package com.fosung.framework.common.secure.auth;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.util.UtilAuthentication;
import com.fosung.framework.common.util.UtilString;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

/**
 * 认证通过用户的管理适配器
 * @Author : liupeng
 * @Date : 2018/8/23 11:21
 * @Modified By
 */
@Slf4j
public abstract class AppUserDetailsServiceAdaptor implements AppUserDetailsService {

    @Getter
    @Autowired
    protected AppSecureProperties appSecureProperties ;

    /**
     * 获取当前登录的用户信息
     * @return
     */
    @Override
    public AppUserDetails getAppUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication() ;

        // 认证信息为空 、 没有经过认证 、 认证的实体不是指定的类型
//        if( authentication==null || authentication.getPrincipal()==null ||
//                !( authentication.getPrincipal() instanceof AppUserDetails ) ){
//            log.error("获取认证后的用户信息为空");
//            return null ;
//        }

        if( authentication==null ){
            log.error("获取认证后的 authentication=null");
            return null ;
        }

        if( authentication.getPrincipal()==null ){
            log.error("获取认证后的 authentication.getPrincipal()==null");
            return null ;
        }

        if( !( authentication.getPrincipal() instanceof AppUserDetails ) ){
            log.debug("获取认证后的 {}.getPrincipal() = {}" , authentication.getClass().getSimpleName() ,
                    JsonMapper.toJSONString( authentication.getPrincipal() ) );
            return null ;
        }

        return (AppUserDetails) authentication.getPrincipal() ;
    }

    @Override
    public boolean isOAuth2Authentication() {
        AppUserDetails appUserDetails = getAppUserDetails() ;

        return appUserDetails!=null && UtilString.equalsIgnoreCase( appUserDetails.getAuthSource() , "oauth2" ) ;
    }

    /**
     * 装载用户信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 构建app用户登录对象
        AppUserDetailsDefault appUserDetailsDefault = new AppUserDetailsDefault( username ) ;

        // 装载用户信息
        loadUserProperties( appUserDetailsDefault ) ;

        // 检查用户信息的有效性
        checkUserProperties( appUserDetailsDefault ) ;

        // 装载用户权限信息
        loadUserAuthorities( appUserDetailsDefault ) ;

        return appUserDetailsDefault ;
    }

    /**
     * 装载用户详情，填充用户id、密码等属性
     * @param appUserDetailsDefault
     */
    public abstract void loadUserProperties( AppUserDetailsDefault appUserDetailsDefault ) ;

    /**
     * 查验用户属性信息
     * @param appUserDetailsDefault
     */
    public void checkUserProperties( AppUserDetailsDefault appUserDetailsDefault ){
        if( appUserDetailsDefault.getUserId() == null || (
                appUserDetailsDefault.getUserId() instanceof String && UtilString.isBlank( appUserDetailsDefault.getUserId() )
            )){
            throw new UsernameNotFoundException( appUserDetailsDefault.getUsername()+"不存在" ) ;
        }
    }

    /**
     * 装载用户授权信息
     * @param appUserDetailsDefault
     */
    public void loadUserAuthorities( AppUserDetailsDefault appUserDetailsDefault ){
        // 定义用户角色列表
        Set<String> userRoles = Sets.newHashSet( appSecureProperties.getAuth().getDefaultUserRoles() ) ;

        // 装载用户角色信息
        loadUserRoles( appUserDetailsDefault , userRoles ) ;

        // 添加用户角色授权
        appUserDetailsDefault.getAuthorities().addAll(
                UtilAuthentication.convertRoleToGrantedAuthority( userRoles ) ) ;
    }

    /**
     * 装载用户角色列表
     * @param appUserDetailsDefault
     */
    public void loadUserRoles(AppUserDetailsDefault appUserDetailsDefault , Set<String> userRoles){

    }


}
