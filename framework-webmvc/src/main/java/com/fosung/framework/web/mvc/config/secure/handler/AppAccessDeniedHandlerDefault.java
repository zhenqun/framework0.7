package com.fosung.framework.web.mvc.config.secure.handler;

import com.fosung.framework.web.http.ResponseParam;
import com.fosung.framework.web.util.UtilWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AppAccessDeniedHandlerDefault extends AccessDeniedHandlerImpl {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("{} 没有访问权限,拒绝此请求" , request.getRequestURI());

        if( !UtilWeb.isAjaxRequest( request ) ){
            if (!response.isCommitted()) {
                response.setContentType( "text/html; charset=UTF-8" ) ;
                response.setCharacterEncoding("UTF-8");
                response.sendError(HttpStatus.FORBIDDEN.value(),
                        "您没有 "+request.getRequestURI()+" 的访问权限");
            }
        }else{
            UtilWeb.writeToResponse( response ,
                ResponseParam.fail().message("您没有 "+ request.getRequestURI() +" 的访问权限").getResponseEntity( HttpStatus.FORBIDDEN )
            );
        }
    }
}
