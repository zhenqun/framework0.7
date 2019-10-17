package com.fosung.framework.web.mvc.config.secure;
/**
 * 包含的filter名称
 */
public enum AppSecureFilter {
	anon("不需要权限过滤") , roles("角色过滤") , logineduser("任何登录的用户可访问") ;
//	rolebasedlogin("基于不同的角色为登录url添加不同的参数") , loginauth("登录权限过滤，可以启动验证码验证功能") ,
//	logout("退出过滤") ,crossdomain("跨域访问接口") , session("验证请求上下文中是否存在session，不存在则创建") ;

	/**
	 * 对当前过滤器的描述
	 */
	public String description ;
	
	AppSecureFilter(String description){
		this.description = description ;
	}
}