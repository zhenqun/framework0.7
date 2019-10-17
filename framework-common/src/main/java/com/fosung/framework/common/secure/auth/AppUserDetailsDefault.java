package com.fosung.framework.common.secure.auth;

import com.fosung.framework.common.secure.auth.support.UserAuth;
import com.fosung.framework.common.secure.auth.support.UserOrg;
import com.fosung.framework.common.util.UtilAuthentication;
import com.fosung.framework.common.util.UtilString;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 默认用户详情
 * @Author : liupeng
 * @Date : 2018-10-16
 * @Modified By
 */
@Setter
@Getter
public class AppUserDetailsDefault implements AppUserDetails {

    private static final long serialVersionUID = 1L;

    // 未过期
    private boolean accountNonExpired = true ;

    // 未锁定
    private boolean accountNonLocked = true ;

    // 密码未失效
    private boolean credentialsNonExpired = true ;

    // 账号是否允许
    private boolean enabled = true ;

    // 登录用户名
    private String username ;

    // 用户密码
    private String password ;

    // 手机号
    private String telephone ;

    // 用户真实姓名 默认使用登录用户名
    private String userRealName ;

    // 用户id
    private Object userId ;

    // 用户来源
    private String userSource ;

    // 用户头像地址
    private String avatar ;

    // 组织机构id
    private String orgId ;

    // 组织机构编码
    private String orgCode ;

    // 组织机构名称
    private String orgName ;

    // 认证来源，默认为当前系统登录，可选方式 login 、 oauth2
    private String authSource = "login" ;

    // 客户端注册id
    private String clientRegistrationId = "" ;

    // 用户其它属性
    private Map<String,Object> userProperties = Maps.newHashMap() ;

    private List<UserAuth> userAuths = Lists.newArrayList() ;

    private List<UserOrg> userOrgs = Lists.newArrayList() ;

    // 获取权限信息，包括管理组织机构及对应的角色信息等
    private List<Object> userPermissions = Lists.newArrayList() ;

    // 用户权限列表
    private Collection<GrantedAuthority> authorities = Lists.newArrayList() ;

    public AppUserDetailsDefault(String username){
        this.username = username ;
    }

    /**
     * 获取用户的 id
     * @param <ID>
     * @return
     */
    @Override
    public <ID> ID getUserId() {
        return userId==null ? null : (ID)userId ;
    }

    @Override
    public String getUserRealName() {
        return UtilString.isBlank( userRealName ) ? username : userRealName ;
    }

    /**
     * 设置用户属性
     * @param userProperties
     */
    public void setUserProperties( Map<String,Object> userProperties ){
        if( userProperties != null ){
            this.userProperties.putAll( userProperties ) ;
        }
    }

    /**
     * 添加用户属性
     * @param key 属性名称
     * @param value 属性值
     * @return
     */
    public Map<String,Object> addUserProperty(String key , Object value){
        this.userProperties.put( key , value ) ;
        return this.userProperties ;
    }

    @Override
    public Set<String> getUserRoleCodes() {
        return UtilAuthentication.convertGrantedAuthorityToRole( this.authorities ) ;
    }
}
