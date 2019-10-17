package com.fosung.framework.web.mvc.config.secure.filter.support;

import com.fosung.framework.web.mvc.config.secure.exception.VCodeAuthenticationException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class DefaultVCodeValidator implements AuthenticateValidator {

    @Setter
    @Getter
    private String vcodeParam = "vcode" ;

    @Setter
    @Getter
    private String vcodeParamInSession = "vcodelogin" ;

    @Override
    public boolean valid(ServletRequest request , String principal) throws VCodeAuthenticationException {

        if(StringUtils.isBlank(getVCode(request)) || StringUtils.isBlank(getVCodeInSession(request)) ||
                !StringUtils.equalsIgnoreCase( getVCode(request) , getVCodeInSession(request) )){
            throw  new VCodeAuthenticationException("验证码错误") ;
        }

        return true ;
    }

    /**
     * 获取request请求中的验证码参数值
     * @param request
     * @return
     */
    protected String getVCode(ServletRequest request) {
        String vcode = request.getParameter(getVcodeParam()) ;
        return vcode!=null ? vcode.trim().toUpperCase() : null ;
    }

    /**
     * 获取session中存储的验证码参数值
     * @param request
     * @return
     */
    protected String getVCodeInSession(ServletRequest request){
        HttpServletRequest httpServletRequest = (HttpServletRequest)request ;
        Object vcodeInSession = httpServletRequest.getSession().getAttribute(getVcodeParamInSession()) ;
        if(vcodeInSession!=null){
            return vcodeInSession.toString().trim().toLowerCase() ;
        }
        return null ;
    }
}
