package com.fosung.framework.web.mvc.config.secure.firewall.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.firewall.FirewalledRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 应用请求的防火墙
 * @Author : liupeng
 * @Date : 2018-10-16
 * @Modified By
 */
@Slf4j
public class AppFirewalledRequest extends FirewalledRequest {

    public AppFirewalledRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public void reset() {
        if( getRequest() instanceof FirewalledRequest ){
            ((FirewalledRequest)getRequest()).reset();
        }
    }
}
