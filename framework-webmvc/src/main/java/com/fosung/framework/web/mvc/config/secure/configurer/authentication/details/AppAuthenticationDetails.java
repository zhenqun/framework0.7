package com.fosung.framework.web.mvc.config.secure.configurer.authentication.details;

import com.fosung.framework.web.util.UtilWeb;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

@Setter
@Getter
public class AppAuthenticationDetails implements Serializable {

    private static final long serialVersionUID = 0L ;

    private Map<String, Object> requestParams = Maps.newHashMap() ;

    public AppAuthenticationDetails(){

    }

    public AppAuthenticationDetails(HttpServletRequest request){
        requestParams = UtilWeb.getParametersStartingWith( request , "" ) ;
    }

    /**
     * 获取请求参数
     * @param param
     * @return
     */
    public Object getParameter(String param){
        return requestParams.get( param ) ;
    }

}
