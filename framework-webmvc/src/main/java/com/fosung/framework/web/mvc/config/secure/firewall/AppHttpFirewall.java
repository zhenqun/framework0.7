package com.fosung.framework.web.mvc.config.secure.firewall;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.web.mvc.config.secure.firewall.support.AppFirewalledRequest;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 应用级别的防火墙
 * @Author : liupeng
 * @Date : 2018-10-16
 * @Modified By
 */
@Slf4j
public class AppHttpFirewall implements HttpFirewall {

    private List<HttpFirewall> httpFirewalls = Lists.newArrayList() ;

    protected AppSecureProperties appSecureProperties ;

    public AppHttpFirewall(ApplicationContext applicationContext , AppSecureProperties appSecureProperties){
        this.appSecureProperties = appSecureProperties ;

        httpFirewalls.add( new AppHttpUrlFirewall( applicationContext , appSecureProperties ) ) ;

        httpFirewalls.add( new AppHttpRefererFirewall( applicationContext , appSecureProperties ) ) ;

        httpFirewalls.add( new AppHttpSignatureFirewall( applicationContext , appSecureProperties ) ) ;

        httpFirewalls.add( new AppHttpXSSFirewall( applicationContext , appSecureProperties ) ) ;

    }

    @Override
    public FirewalledRequest getFirewalledRequest(HttpServletRequest request) throws RequestRejectedException {

        log.info("请求uri: {}" , request.getRequestURI()) ;

        FirewalledRequest firewalledRequest = null ;

        try{
            // 构建 firewalledRequest 请求
            for (HttpFirewall httpFirewall : httpFirewalls) {

                if( firewalledRequest == null ){
                    firewalledRequest = httpFirewall.getFirewalledRequest( request ) ;
                }else{
                    firewalledRequest = httpFirewall.getFirewalledRequest( firewalledRequest ) ;
                }

            }

        }catch( RequestRejectedException e ){
            // 拦截异常后，依旧正常抛出
            log.error( "请求不是有效请求 : {} , {}" , request.getRequestURL() , e.getMessage() ) ;
            throw e ;
        }

        return firewalledRequest==null ? new AppFirewalledRequest( request ) : firewalledRequest ;
    }

    @Override
    public HttpServletResponse getFirewalledResponse(HttpServletResponse response) {
        return response ;
    }
}
