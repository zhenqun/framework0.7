package com.fosung.framework.common.util;

import com.fosung.framework.common.secure.auth.AppUserDetailsService;
import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
/**
 * 认证信息帮助类
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public class UtilAuthentication {

    /**
     * 将字符串格式的角色编码转换为授权对象
     * @param userRoleCodes
     * @return
     */
    public static Collection<GrantedAuthority> convertRoleToGrantedAuthority( Set<String> userRoleCodes ) {
        if( userRoleCodes==null ){
            return Sets.newHashSet() ;
        }
        Set<GrantedAuthority> grantedAuthorities = Sets.newHashSet() ;

        // 添加用户角色授权
        userRoleCodes.stream().filter( role-> UtilString.isNotBlank(role) ).forEach(role->{
            role = role.trim() ;
            // 角色编码不是以 ROLE_ 开头，则动态添加一个角色编码头信息
            if( !role.startsWith( AppUserDetailsService.ROLE_CODE_PREFIX ) ){
                role = AppUserDetailsService.ROLE_CODE_PREFIX + role ;
            }
            grantedAuthorities.add( new SimpleGrantedAuthority( role )) ;
        }) ;

        return grantedAuthorities ;
    }

    /**
     * 将字符串格式的授权对象转换为角色编码
     * @return
     */
    public static Set<String> convertGrantedAuthorityToRole( Collection<? extends GrantedAuthority> grantedAuthorities ){
        if( grantedAuthorities==null ){
            return Sets.newHashSet() ;
        }

        Set<String> userRoles = Sets.newHashSet() ;

        grantedAuthorities.stream().filter( grantedAuthority -> grantedAuthority!=null &&
                UtilString.startsWith( grantedAuthority.getAuthority() , AppUserDetailsService.ROLE_CODE_PREFIX))
            .forEach( grantedAuthority -> {
                userRoles.add( UtilString.removeStart( grantedAuthority.getAuthority() , AppUserDetailsService.ROLE_CODE_PREFIX ) ) ;
            } );

        return userRoles ;
    }

}
