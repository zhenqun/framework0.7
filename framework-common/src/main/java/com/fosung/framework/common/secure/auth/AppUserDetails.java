package com.fosung.framework.common.secure.auth;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * 认证后的用户
 * @Author : liupeng
 * @Date : 2018/8/23 9:55
 * @Modified By
 */
public interface AppUserDetails extends UserDetails {

    /**
     * 获取用户id
     * @return
     */
    <ID> ID getUserId() ;

    /**
     * 获取用户来源，可能来源于：灯塔、日喀则、其他系统
     * @return
     */
    String getUserSource() ;

    /**
     * 认证来源
     */
    String getAuthSource() ;

    /**
     * 获取oauth2客户端注册的id
     * @return
     */
    String getClientRegistrationId() ;

    /**
     * 获取手机号
     * @return
     */
    String getTelephone() ;

    /**
     * 显示姓名
     * @return
     */
    String getUserRealName() ;

    /**
     * 获取用户角色编码
     * @return
     */
    Set<String> getUserRoleCodes() ;

    /**
     * 获取用户头像
     * @return
     */
    String getAvatar() ;

    /**
     * 获取组织结构id
     * @return
     */
    String getOrgId() ;

    /**
     * 获取组织结构名称
     * @return
     */
    String getOrgName() ;

    /**
     * 获取组织结构编码
     * @return
     */
    String getOrgCode() ;


}
