package com.fosung.framework.web.util;

import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.util.UtilString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * web操作接口类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
public class UtilWeb extends WebUtils {

	public static final PathMatcher PATTERN_MATCHER = new AntPathMatcher() ;

	public static final String HEAD_REFERER_PARAM = "Referer" ;

	/**
	 * 获取请求的referer
	 * @param request
	 * @return
	 */
	public static String getRequestReferer(HttpServletRequest request){
		return request.getHeader(HEAD_REFERER_PARAM) ;
	}

	/**
	 * 获取请求的origin
	 * @param request
	 * @return
	 */
	public static String getRequestOrigin( HttpServletRequest request ){
		return request.getHeader("Origin") ;
	}

	/**
	 * 获取http请求的domain
	 * @param url
	 * @return
	 */
	public static String getRequestSourceDomain(String url){
		if(UtilString.isBlank(url)){
			return null ;
		}

		//移除http的请求头
		url = UtilString.removeStart(url.trim() , "http://") ;
		url = UtilString.removeStart(url , "https://") ;

		//定位域名和服务地址之间的分隔位置
		//截取请求域名
		int lastPathSpliterIndex = url.indexOf("/") ;
		if(lastPathSpliterIndex!=-1){
			url = url.substring( 0 , lastPathSpliterIndex ) ;
		}
		//删掉端口号
		lastPathSpliterIndex = url.lastIndexOf(":") ;
		if(lastPathSpliterIndex!=-1){
			url = url.substring( 0 , lastPathSpliterIndex ) ;
		}

		return url ;
	}

	/**
	 * 判断url是否匹配指定的格式
	 * @param urlPattern
	 * @param url
	 * @return
	 */
	public static boolean urlMatch(String urlPattern , String url){
		if( UtilString.isBlank(urlPattern) || UtilString.isBlank(url) ){
			return false ;
		}
		return PATTERN_MATCHER.match( urlPattern , url) ;
	}

	/**
	 * 获取请求客户端的真实ip地址
	 * @param request
	 * @return
	 */
	public static String getRequestClientIp( HttpServletRequest request ){
		String realIp = request.getHeader("X-Real-IP") ;
		return UtilString.isBlank( realIp ) ? "" : realIp ;
	}

	/**
	 * post请求
	 * @param request
	 * @return
	 */
	public static boolean isPostRequest( HttpServletRequest request ){
		return UtilString.equalsIgnoreCase( request.getMethod() , "post" ) ;
	}
	/**
	 * 判断HTTP请求是否ajax请求，请求头中 X-Requested-With=XMLHttpRequest 或 post请求
	 * @param request http请求对象
	 * @return true或false
	 */
	public static boolean isAjaxRequest(HttpServletRequest request){
		return UtilString.equalsIgnoreCase( "XMLHttpRequest" , request.getHeader("X-Requested-With") ) ||
				isPostRequest( request ) ;
	}
	/**
	 * 为url添加请求上下文地址
	 * @param request http请求
	 * @param url 需要包装上下文路径的url
	 * @return
	 */
	public static String wrapContextPath(HttpServletRequest request ,String url){
		if(UtilString.isBlank(url)){
			return request.getContextPath() ;
		}
		
		if(url.startsWith(request.getContextPath())){
			return url ;
		}
		
		url = request.getContextPath()+"/"+url ;
		
		url = url.replaceAll("/+", "/") ;
		//如果是跟路径，默认不加前缀符号
		if(url.equals("/")){
			url = "" ;
		}
		
		return url ;
	}
	/**
	 * 消除url中的请求上下文路径
	 * @param request http请求
	 * @param url http请求的url
	 * @return
	 */
	public static String unwrapContextPath(HttpServletRequest request ,String url){
		if(UtilString.isBlank(url) || !url.startsWith(request.getContextPath())){
			return url ;
		}
		
		if(!request.getContextPath().equals("/")){
			url = url.substring(request.getContextPath().length()) ;
		}
		
		return url.replaceAll("/+", "/") ;
	}
	
	/**
	 * responseEntity内容转换为json，并使用response返回内容
	 * @param response
	 * @param responseEntity
	 */
	public static void writeToResponse(HttpServletResponse response , ResponseEntity<?> responseEntity){
		//log.info("回写response,状态码:{}" , responseEntity.getStatusCode().value());
		response.setStatus(responseEntity.getStatusCode().value());
		writeToResponse(response , responseEntity.getBody()) ;
	}
	
	/**
	 * content转换为json，使用callbackFN封装参数后，使用response返回内容
	 * @param response response对象
	 * @param content 内容
	 * @param callbackFN jsonp回调函数的名称
	 */
	public static void writeToJSONPResponse(HttpServletResponse response , Object content , String callbackFN){
		response.setCharacterEncoding("UTF-8");
        PrintWriter out;
		try {
			out = response.getWriter();
			if(UtilString.isBlank(callbackFN)){
				out.println(JsonMapper.toJSONString(content));
			}else{
				out.println(callbackFN+"("+JsonMapper.toJSONString(content)+")");
			}
	        out.flush();
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取回调的字符串
	 * @param content
	 * @return
	 */
	public static String getResponseText(HttpServletRequest request , Object content){
		String callback = request ==null ? null : request.getParameter("callback") ;
		
		if(UtilString.isBlank(callback)){
			return JsonMapper.toJSONString(content) ;
		}
		
		return callback+"("+JsonMapper.toJSONString(content)+")" ;
	}
	
	/**
	 * content转换为json，并使用response返回内容
	 * @param response
	 * @param content
	 */
	public static void writeToResponse(HttpServletResponse response , Object content){
		response.setContentType( "text/html; charset=UTF-8" ) ;
		response.setCharacterEncoding("UTF-8");
        PrintWriter out;
		try {
			out = response.getWriter();
			out.println(JsonMapper.toJSONString(content));
	        out.flush();
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * content转换为json，并使用response返回内容
	 * @param response http response对象
	 * @param content reponse输出的内容
	 * @param contentType response内容类型
	 */
	public static void writeToResponse(HttpServletResponse response , Object content , String contentType){
		response.setContentType(contentType);
        writeToResponse(response, content);
	}
}
