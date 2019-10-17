package com.fosung.framework.web.mvc.config.secure.configurer.authentication.details;

import org.springframework.security.authentication.AuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

public class AppAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest,AppAuthenticationDetails> {

    @Override
    public AppAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new AppAuthenticationDetails( context ) ;
    }

}
