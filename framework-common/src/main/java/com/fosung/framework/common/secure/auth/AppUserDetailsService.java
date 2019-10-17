package com.fosung.framework.common.secure.auth;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 认证通过用户的管理
 * @Author : liupeng
 * @Date : 2018/8/23 11:21
 * @Modified By
 */
public interface AppUserDetailsService extends UserDetailsService {

    /**
     * 用户角色编码前缀
     */
    String ROLE_CODE_PREFIX  = "ROLE_" ;

    /**
     * 获取经过系统认证后的用户
     * @return
     */
    AppUserDetails getAppUserDetails() ;

    /**
     * 登录认证来源于oauth2认证
     * @return
     */
    boolean isOAuth2Authentication() ;

}
