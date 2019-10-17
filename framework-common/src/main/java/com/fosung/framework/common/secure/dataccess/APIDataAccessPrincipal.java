package com.fosung.framework.common.secure.dataccess;

import com.fosung.framework.common.secure.auth.AppUserDetails;
import com.fosung.framework.common.util.UtilString;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 数据权限验证需要信息的委托代理类
 * @Author : liupeng
 * @Date : 2018/8/27 10:03
 * @Modified By
 */
@Slf4j
@Getter
@Setter
public class APIDataAccessPrincipal {

    private AppUserDetails appUserDetails ;

    private APIDataAccess apiDataAccess ;

    /**
     * 获取当前登录用户的id
     * @return
     */
    public String getUserId(){
        if( appUserDetails==null ){
            return null ;
        }

        return appUserDetails.getUserId().toString() ;
    }

    /**
     * 获取当前登录用户的角色
     * @return
     */
    public List<String> getUserRoles(){
        if( appUserDetails==null ){
            return null ;
        }

        List<String> userRoles = Lists.newArrayList() ;

        // 获取授权的用户角色
        appUserDetails.getAuthorities().stream()
                .filter( grantedAuthority -> UtilString.startsWith( grantedAuthority.getAuthority() , "ROLE_" ))
                .forEach(grantedAuthority-> {
                    userRoles.add( UtilString.removeStart( grantedAuthority.getAuthority() , "ROLE_" ) ) ;
                } ) ;

        return userRoles ;
    }

    /**
     * 是否为同一用户
     * @param currentUserId
     * @return
     */
    public boolean isSameUser(String currentUserId){
        if( UtilString.isBlank(currentUserId) ){
            return false ;
        }
        return UtilString.equalsIgnoreCase( getUserId() , currentUserId  ) ;
    }

    /**
     * 判断当前登录用户是否有对应的角色权限
     * @param roleCode
     * @return
     */
    public boolean hasRole( String roleCode ){
        return hasAnyRole( roleCode ) ;
    }

    /**
     * 判断当前登录用户是否具有指定角色中的一个
     * @param roleCodes
     * @return
     */
    public boolean hasAnyRole( String... roleCodes ){
        return getUserRoles().stream().filter( userRole-> {
            for (String roleCode : roleCodes) {
                if( UtilString.isBlank( roleCode ) ){
                    continue;
                }
                if( UtilString.equalsIgnoreCase( userRole , roleCode ) ){
                    return true ;
                }
            }
            return false ;
        }).count() > 0 ;
    }

    /**
     * APIDataAccessPrincipal 构造器
     * @Author : liupeng
     * @Date : 2018/8/27 10:02
     * @Modified By
     */
    public static class APIDataAccessPrincipalBuilder {
        private APIDataAccessPrincipal apiDataAccessPrincipal = null;

        private APIDataAccessPrincipalBuilder() {
            apiDataAccessPrincipal = new APIDataAccessPrincipal();
        }

        public APIDataAccessPrincipalBuilder appUserDetails(AppUserDetails appUserDetails) {
            apiDataAccessPrincipal.setAppUserDetails( appUserDetails );
            return this;
        }

        public APIDataAccessPrincipalBuilder apiDataAccess(APIDataAccess apiDataAccess) {
            apiDataAccessPrincipal.setApiDataAccess(apiDataAccess);
            return this;
        }

        public final APIDataAccessPrincipal build() {
            return apiDataAccessPrincipal;
        }

        public static APIDataAccessPrincipalBuilder newBuilder() {
            // 不对用户信息进行有效性校验，在实际权限校验时，对值有效性进行判断
            return new APIDataAccessPrincipalBuilder() ;
        }
    }

}
