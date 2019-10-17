package com.fosung.framework.web.mvc.config.secure.dataaccess;

import com.fosung.framework.common.secure.auth.AppUserDetailsService;
import com.fosung.framework.common.secure.dataccess.APIDataAccess;
import com.fosung.framework.common.secure.dataccess.APIDataAccessPrincipal;
import com.fosung.framework.common.secure.dataccess.APIDataAccessPrincipalHolder;
import com.fosung.framework.common.util.UtilAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户信息拦截器
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
public class APIDataAccessPrincipalInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果handler为空或者不是一个HandMentod，则不添加过滤器
        if (handler == null || !(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //获取用户过滤信息
        APIDataAccess apiDataAccess = UtilAnnotation.findAnnotationWithCascade( handlerMethod.getMethod() , APIDataAccess.class ) ;

        if( apiDataAccess !=null ){

            ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext( request.getServletContext() ) ;

            AppUserDetailsService appUserDetailsService = applicationContext.getBean( AppUserDetailsService.class ) ;

            // 获取认证后的用户信息
            APIDataAccessPrincipal apiDataAccessPrincipal = APIDataAccessPrincipal.APIDataAccessPrincipalBuilder.newBuilder()
                    .appUserDetails( appUserDetailsService.getAppUserDetails() )
                    .apiDataAccess( apiDataAccess )
                    .build() ;

            APIDataAccessPrincipalHolder.setPrincipalHolder( apiDataAccessPrincipal );
        }

        return true ;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        APIDataAccessPrincipalHolder.clearAPIDataAccessPrincipalHolder();
    }


}
