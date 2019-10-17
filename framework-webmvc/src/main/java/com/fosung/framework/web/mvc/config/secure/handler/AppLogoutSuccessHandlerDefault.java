package com.fosung.framework.web.mvc.config.secure.handler;

import com.fosung.framework.web.http.ResponseParam;
import com.fosung.framework.web.util.UtilWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AppLogoutSuccessHandlerDefault implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("退出成功: {}" , request.getSession().getId()) ;

        UtilWeb.writeToResponse( response ,
                ResponseParam.success().message("退出成功")
                .getResponseEntity(HttpStatus.OK) ) ;
    }
}
