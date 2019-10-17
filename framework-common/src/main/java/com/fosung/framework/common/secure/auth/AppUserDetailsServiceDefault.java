package com.fosung.framework.common.secure.auth;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 默认的登录用户认证 和 获取
 * @Author : liupeng
 * @Date : 2018/8/23 11:21
 * @Modified By
 */
@Slf4j
public class AppUserDetailsServiceDefault extends AppUserDetailsServiceAdaptor {

    /**
     * 装载用户详情，填充用户id、密码等属性
     * @param appUserDetailsDefault
     */
    @Override
    public void loadUserProperties( AppUserDetailsDefault appUserDetailsDefault ) {

    }

    /**
     * 装载用户角色列表
     * @param appUserDetailsDefault
     */
    @Override
    public void loadUserRoles(AppUserDetailsDefault appUserDetailsDefault , Set<String> userRoles){

    }


}
