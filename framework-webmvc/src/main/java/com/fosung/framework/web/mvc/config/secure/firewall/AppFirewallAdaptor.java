package com.fosung.framework.web.mvc.config.secure.firewall;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.web.mvc.config.secure.firewall.support.AppFirewalledRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基础防火墙适配器
 * @Author : liupeng
 * @Date : 2018-10-16
 * @Modified By
 */
public abstract class AppFirewallAdaptor implements HttpFirewall {

    @Setter
    @Getter
    protected AppSecureProperties appSecureProperties ;

    @Getter
    @Setter
    protected ApplicationContext applicationContext ;

    public AppFirewallAdaptor(ApplicationContext applicationContext ,AppSecureProperties appSecureProperties){
        this.appSecureProperties = appSecureProperties ;
        this.applicationContext = applicationContext ;
    }

    /**
     * 是否允许进行防火墙拦截
     * @param request
     * @return
     */
    public boolean isEnable(HttpServletRequest request){
        return true ;
    }

    @Override
    public FirewalledRequest getFirewalledRequest( HttpServletRequest request ) throws RequestRejectedException {
        if( !isEnable(request) ){
            return emptyFirewalledRequest( request ) ;
        }

        return doWrapRequest( request ) ;
    }

    /**
     * 获取空的 防火墙请求
     * @param request
     * @return
     */
    public FirewalledRequest emptyFirewalledRequest( HttpServletRequest request ){
        if( request instanceof FirewalledRequest ){
            return (FirewalledRequest)request ;
        }else{
            return new AppFirewalledRequest( request ) ;
        }
    }

    /**
     * 获取防火墙请求
     * @param request
     * @return
     */
    public abstract FirewalledRequest doWrapRequest( HttpServletRequest request ) ;

    @Override
    public HttpServletResponse getFirewalledResponse(HttpServletResponse response) {
        return response;
    }
}
