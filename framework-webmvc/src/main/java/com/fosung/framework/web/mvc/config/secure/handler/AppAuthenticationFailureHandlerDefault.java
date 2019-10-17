package com.fosung.framework.web.mvc.config.secure.handler;

import com.fosung.framework.web.http.ResponseParam;
import com.fosung.framework.web.util.UtilWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AppAuthenticationFailureHandlerDefault implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("认证失败: {}" , exception.getMessage() ); ;

        UtilWeb.writeToResponse( response , ResponseParam.fail()
                .message( getExceptionMessage( exception ) )
                .code( exception.getClass().getSimpleName() )
                .getResponseEntity( HttpStatus.OK ) ) ;
    }

    /**
     * 获取异常消息内容
     * @param authenticationException
     * @return
     */
    public String getExceptionMessage( AuthenticationException authenticationException ){

        authenticationException.printStackTrace() ;

        if( authenticationException instanceof BadCredentialsException){
            return "用户名或密码错误" ;
        }

        if( authenticationException instanceof LockedException ){
            return "账号已经被锁定" ;
        }

        if( authenticationException instanceof DisabledException){
            return "账号被禁用" ;
        }

        if( authenticationException instanceof CredentialsExpiredException ){
            return "账号密码失效" ;
        }

        if( authenticationException instanceof AccountExpiredException){
            return "账号用户名已经到期" ;
        }

        return authenticationException.getMessage() ;
    }

}
