package com.fosung.framework.web.mvc.config.secure.filter.support;

import javax.servlet.ServletRequest;

/**
 * 认证通过后的处理。需要在 AjaxFormAuthenticationFilter 中设置。
 */
public interface AuthenticateValidator {
	/**
	 * 判断请求是否有效
	 * @param request
	 * @param principal 当前登录用户的名称
	 * @return
	 */
	boolean valid(ServletRequest request, String principal) ;
}
