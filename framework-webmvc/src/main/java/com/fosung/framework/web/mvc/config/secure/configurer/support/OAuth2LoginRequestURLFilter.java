package com.fosung.framework.web.mvc.config.secure.configurer.support;

import com.fosung.framework.common.config.AppSecureProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

@Slf4j
public class OAuth2LoginRequestURLFilter implements Filter {

    private AppSecureProperties appSecureProperties ;

    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
            new HttpSessionOAuth2AuthorizationRequestRepository();

    public OAuth2LoginRequestURLFilter(AppSecureProperties appSecureProperties) {
        this.appSecureProperties= appSecureProperties;
    }

    @Override
    public void init(FilterConfig filterConfiguration) throws ServletException {
        log.info("OAuth2LoginRequestURLFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 验证是否为oauth2请求
        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository.loadAuthorizationRequest(
                (HttpServletRequest) request) ;

        if( authorizationRequest!=null ){
            request = new OAuth2RequestWrapper( (HttpServletRequest) request , authorizationRequest.getRedirectUri() );
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    class OAuth2RequestWrapper extends HttpServletRequestWrapper {

        public String redirectUrl;

        public OAuth2RequestWrapper(HttpServletRequest request , String sourceRedirectUri) {
            super(request);
            this.redirectUrl = sourceRedirectUri ;
        }

        @Override
        public StringBuffer getRequestURL() {
            //log.info("获取封装后的请求地址: {}" , redirectUrl);
            return new StringBuffer( this.redirectUrl );
        }
    }

//    /**
//     * 重新扩展请求uri
//     * @param request
//     * @return
//     */
//    private String expandRedirectUri(HttpServletRequest request ) {
//        int port = request.getServerPort();
//        if (("http".equals(request.getScheme()) && port == 80) || ("https".equals(request.getScheme()) && port == 443)) {
//            port = -1;
//        }
//
//        String baseUrl = UriComponentsBuilder.newInstance()
//                .scheme(request.getScheme())
//                .host(request.getServerName())
//                .port(port)
//                .path(request.getContextPath())
//                .build()
//                .toUriString();
//
//        Map<String, String> uriVariables = new HashMap<>();
//        uriVariables.put("baseUrl", baseUrl);
//
//        return UriComponentsBuilder.fromUriString( appSecureProperties.getSso().getRedirectUriTemplate() )
//                .buildAndExpand(uriVariables)
//                .toUriString();
//    }
//
//    class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {
//
//        public String redirectUrl;
//
//        public MyHttpServletRequestWrapper(HttpServletRequest request) {
//            super(request);
//            if( UtilString.isNotBlank( appSecureProperties.getSso().getRedirectUriTemplate() ) ){
//                this.redirectUrl = expandRedirectUri(request) ;
//            }
//        }
//
//        @Override
//        public StringBuffer getRequestURL() {
//            if(UtilString.isBlank( redirectUrl )){
//                return super.getRequestURL() ;
//            }
//            //log.info("获取封装后的请求地址: {}" , redirectUrl);
//            return new StringBuffer(redirectUrl);
//        }
//    }

}
