package com.fosung.framework.web.mvc.config.session;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.web.common.filter.AppWebFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 应用session的配置
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Configuration
@Slf4j
@AutoConfigureOrder( Ordered.HIGHEST_PRECEDENCE )
@EnableRedisHttpSession
public class AppSessionAutoConfiguration {

    public static final String SESSION_RESPONSE  = "session_response" ;

    @Bean
    public SessionRequestConfig sessionRequestConfig(){
        return new SessionRequestConfig() ;
    }

//    /**
//     * session的value序列化
//     * @return
//     */
//    @Bean
//    public StringRedisSerializer springSessionDefaultRedisSerializer() {
//        return new GenericToStringSerializer();
//    }

    @Bean
    public AppHttpSessionIdResolverDelegate appHttpSessionIdResolverDelegate( AppProperties appProperties ){
        AppHttpSessionIdResolverDelegate appHttpSessionIdResolverDelegate = new AppHttpSessionIdResolverDelegate() ;

        AppSessionCookieSerializer appSessionCookieSerializer = new AppSessionCookieSerializer( appProperties ) ;
        //cookie值使用httponly
        appSessionCookieSerializer.setUseHttpOnlyCookie( true ) ;
        //使用base64
        appSessionCookieSerializer.setUseBase64Encoding( appProperties.getSession().isCookieSessionEncode() ) ;
        //cookie名称
        appSessionCookieSerializer.setCookieName( appProperties.getSession().getCookieKey() ) ;
        //创建cookie解析器
        CookieHttpSessionIdResolver cookieHttpSessionIdResolver = new CookieHttpSessionIdResolver() ;
        cookieHttpSessionIdResolver.setCookieSerializer( appSessionCookieSerializer ) ;

        // 分别设置header 和 cookie的session解析器
        appHttpSessionIdResolverDelegate.setHeaderHttpSessionIdResolver( new HeaderHttpSessionIdResolver(appProperties.getSession().getHeaderKey())) ;
        appHttpSessionIdResolverDelegate.setCookieHttpSessionIdResolver( cookieHttpSessionIdResolver ) ;

        log.info("创建header 和 cookie的session解析器,cookie解析key={},header解析key={}" ,
                appProperties.getSession().getCookieKey() , appProperties.getSession().getHeaderKey()) ;

        return appHttpSessionIdResolverDelegate ;
    }

//    @Bean
//    public CookieSerializer createCookieSerializer(AppProperties appProperties) {
//        AppSessionCookieSerializer appSessionCookieSerializer = new AppSessionCookieSerializer( appProperties ) ;
//
//        //cookie值使用httponly
//        appSessionCookieSerializer.setUseHttpOnlyCookie( true ) ;
//
//        //使用base64
//        appSessionCookieSerializer.setUseBase64Encoding( appProperties.getSession().isCookieSessionEncode() ) ;
//
//        //cookie名称
//        appSessionCookieSerializer.setCookieName( appProperties.getSession().getCookieKey() ) ;
//
//        return appSessionCookieSerializer;
//    }

    /**
     * 配置session需要request的response参数
     */
    static class SessionRequestConfig extends AppWebFilter {

        @Override
        public boolean doFilterInternal(HttpServletRequest request, HttpServletResponse response) {
            request.setAttribute( SESSION_RESPONSE , response ) ;

            return true;
        }
    }

}
