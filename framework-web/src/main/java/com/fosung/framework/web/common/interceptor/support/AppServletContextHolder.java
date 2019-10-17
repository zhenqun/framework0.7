package com.fosung.framework.web.common.interceptor.support;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求上下文容器，包含当前请求的request和response对象
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class AppServletContextHolder {
	private static ThreadLocal<ServletRequest> requestContainer = new ThreadLocal<ServletRequest>() ;
	
	private static ThreadLocal<ServletResponse> responseContainer = new ThreadLocal<ServletResponse>() ;
	
	/**
	 * 设置当前请求的上下文
	 */
	public static void setServletContext(ServletRequest servletRequest , ServletResponse servletResponse){
		setServletRequest(servletRequest) ;
		setServletResponse(servletResponse) ;
	}
	/**
	 * 设置当前请求的request对象
	 * @param servletRequest
	 */
	public static void setServletRequest(ServletRequest servletRequest){
		requestContainer.set(servletRequest);
	}
	/**
	 * 设置当前请求的response对象
	 * @param servletResponse
	 */
	public static void setServletResponse(ServletResponse servletResponse){
		responseContainer.set(servletResponse);
	}
	/**
	 * 清理请求的上下文
	 */
	public static void clearServletContext(){
		clearServletRequest(); 
		clearServletResponse();
	}
	/**
	 * 请求请求对象
	 */
	public static void clearServletRequest(){
		requestContainer.remove(); 
	}
	/**
	 * 请求response对象
	 */
	public static void clearServletResponse(){
		responseContainer.remove(); 
	}
	
	/**
	 * 获取当前请求的ServletRequest对象对象
	 * @return
	 */
	public static ServletRequest getServletRequest() {
		return requestContainer.get() ;
	}
	
	/**
	 * 获取当前请求的HttpServletRequest对象对象
	 * @return
	 */
	public static HttpServletRequest getHttpServletRequest() {
		if(requestContainer.get()!=null && HttpServletRequest.class.isAssignableFrom(requestContainer.get().getClass())){
			return (HttpServletRequest)requestContainer.get() ;
		}
		return null ;
	}
	
	/**
	 * 获取当前请求的ServletResponse对象对象
	 * @return
	 */
	public static ServletResponse getServletResponse() {
		return responseContainer.get() ;
	}
	
	/**
	 * 获取当前请求的HttpServletResponse对象对象
	 * @return
	 */
	public static HttpServletResponse getHttpServletResponse() {
		if(responseContainer.get() !=null && HttpServletResponse.class.isAssignableFrom(responseContainer.get().getClass())){
			return (HttpServletResponse)responseContainer.get() ;
		}
		return null ;
	}
}
