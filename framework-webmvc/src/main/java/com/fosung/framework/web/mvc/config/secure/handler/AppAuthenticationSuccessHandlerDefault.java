package com.fosung.framework.web.mvc.config.secure.handler;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.secure.auth.AppUserDetailsService;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.http.ResponseParam;
import com.fosung.framework.web.util.UtilWeb;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class AppAuthenticationSuccessHandlerDefault extends SavedRequestAwareAuthenticationSuccessHandler implements InitializingBean {

    @Autowired
    private AppSecureProperties appSecureProperties ;

    @Autowired(required = false)
    protected AppUserDetailsService appUserDetailsService ;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("认证成功: {} ," , JsonMapper.toJSONString( authentication  ) ) ;

        // 只是通过判断是否为post请求，返回不同类型的数据
        if( UtilString.equalsIgnoreCase( request.getMethod() , "post") ){
            log.debug("当前登录认证请求为 ajax 请求, 返回 json 数据");
            onPostAuthenticationSuccess( request , response , authentication ) ;
        }else{
//            String defaultTargetUrl = appSecureProperties.getSso().getLoginSuccessUrl() ;
//            // 获取注册的客户端id
//            if( authentication instanceof OAuth2AuthenticationToken){
//                OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken)authentication ;
//                String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId() ;
//                AppSecureProperties.OAuth2Config oauth2Config = findOAuth2ConfigByRegistrationId( authorizedClientRegistrationId ) ;
//                if( oauth2Config!=null && UtilString.isNotBlank( oauth2Config.getFrontPageUrl() )){
//                    // 设置前端页面地址
//                    defaultTargetUrl = oauth2Config.getFrontPageUrl() ;
//                }
//
//                log.info("当前登录认证请求为oauth2登录页面:{}, {}" , authorizedClientRegistrationId, defaultTargetUrl);
//            }else{
//                log.info("当前登录认证请求为普通页面, 跳转页面到: {}" , defaultTargetUrl);
//            }
            //设置默认的登录页面
            setDefaultTargetUrl( getSuccessUrl( authentication ) );

            super.onAuthenticationSuccess( request , response , authentication ) ;
        }

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

    /**
     * post 请求认证处理
     * @param request
     * @param response
     * @param authentication
     */
    public void onPostAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        Map<String,Object> loginResultData = Maps.newHashMap() ;

//        if( authentication.getPrincipal() != null && authentication.getPrincipal() instanceof AppUserDetails ){
//            AppUserDetails appUserDetails = (AppUserDetails) authentication.getPrincipal() ;
//        }
//        loginResultData.put("authorities" , authentication.getAuthorities()) ;

        UtilWeb.writeToResponse( response , ResponseParam.success()
                .message("登录成功")
                .getResponseEntity(HttpStatus.OK) ) ;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 如果是页面请求，登录成功之后使用默认的登录成功地址
        setAlwaysUseDefaultTargetUrl( true ) ;
        // 默认登录成功的地址，使用一般认证的地址
        setDefaultTargetUrl( appSecureProperties.getAuth().getSuccessUrl()) ;
    }

    /**
     * 获取登录成功的访问地址
     * @return
     */
    public String getSuccessUrl(Authentication authentication){
        String successUrl = appSecureProperties.getAuth().getSuccessUrl() ;
        // 获取注册的客户端id
        if( authentication instanceof OAuth2AuthenticationToken){
            OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken)authentication ;
            String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId() ;
            AppSecureProperties.OAuth2Config oauth2Config = findOAuth2ConfigByRegistrationId( authorizedClientRegistrationId ) ;
            if( oauth2Config!=null && UtilString.isNotBlank( oauth2Config.getFrontPageUrl() )){
                // 设置前端页面地址
                successUrl = oauth2Config.getFrontPageUrl() ;
            }

            // 如果没有找到指定的登录地址，则默认使用sso统一的登录成功地址
            if( UtilString.isBlank( successUrl ) ){
                successUrl = appSecureProperties.getSso().getLoginSuccessUrl() ;
            }

            log.info("当前登录认证请求为oauth2登录页面:{}, {}" , authorizedClientRegistrationId, successUrl);
        }else{
            log.info("当前登录认证请求为普通页面, 跳转页面到: {}" , successUrl);
        }

        return successUrl ;
    }
}
