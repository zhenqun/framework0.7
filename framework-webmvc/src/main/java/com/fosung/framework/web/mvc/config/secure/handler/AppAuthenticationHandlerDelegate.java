package com.fosung.framework.web.mvc.config.secure.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 应用认证处理
 */
@Slf4j
@Setter
@Getter
public class AppAuthenticationHandlerDelegate implements AuthenticationSuccessHandler ,
        AuthenticationFailureHandler , LogoutSuccessHandler ,
        AccessDeniedHandler {

    private List<AuthenticationSuccessHandler> authenticationSuccessHandlers ;

    private List<AuthenticationFailureHandler> authenticationFailureHandlers ;

    private List<LogoutSuccessHandler> logoutSuccessHandlers ;

    private List<AccessDeniedHandler> accessDeniedHandlers ;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if( authenticationSuccessHandlers == null ){
            return ;
        }
        for (AuthenticationSuccessHandler authenticationSuccessHandler : authenticationSuccessHandlers) {
            try{
                authenticationSuccessHandler.onAuthenticationSuccess( request , response , authentication );
            }catch( Exception e ){
               log.error("认证成功处理异常" , e);
            }
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if( authenticationFailureHandlers == null ){
            return;
        }
        for (AuthenticationFailureHandler authenticationFailureHandler : authenticationFailureHandlers) {
            try{
                authenticationFailureHandler.onAuthenticationFailure( request , response , exception );
            }catch( Exception e ){
                log.error("认证失败处理异常" , e);
            }
        }
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if( logoutSuccessHandlers == null ){
            return ;
        }
        for (LogoutSuccessHandler logoutSuccessHandler : logoutSuccessHandlers) {
            try{
                logoutSuccessHandler.onLogoutSuccess( request , response , authentication );
            }catch( Exception e ){
                log.error("退出处理异常" , e);
            }
        }

    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if( accessDeniedHandlers==null ){
            return ;
        }
        for (AccessDeniedHandler accessDeniedHandler : accessDeniedHandlers) {
            try{
                accessDeniedHandler.handle( request , response , accessDeniedException );
            }catch( Exception e ){
                log.error("退出处理异常" , e);
            }
        }
    }
}
