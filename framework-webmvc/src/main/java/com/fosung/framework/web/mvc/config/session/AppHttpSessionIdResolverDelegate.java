package com.fosung.framework.web.mvc.config.session;

import com.fosung.framework.common.util.UtilString;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * 应用的HttpsessionId解析
 * @Author : liupeng
 * @Date : 2019-03-16
 * @Modified By
 */
@Setter
@Getter
@Slf4j
public final class AppHttpSessionIdResolverDelegate implements HttpSessionIdResolver {

	private HeaderHttpSessionIdResolver headerHttpSessionIdResolver ;

	private CookieHttpSessionIdResolver cookieHttpSessionIdResolver ;

	@Override
	public List<String> resolveSessionIds(HttpServletRequest request) {
		// 首先解析header中sessionId
		List<String> sessionIds = headerHttpSessionIdResolver.resolveSessionIds( request ) ;
		// 如果是空的sessionId，则从cookie中解析
		if( isEmpty( sessionIds ) ){
			sessionIds = cookieHttpSessionIdResolver.resolveSessionIds( request ) ;
			log.debug("请求{}, session解析来源cookie : {}" , request.getRequestURI() ,
					sessionIds!=null && sessionIds.size()>0 ? sessionIds.get(0) : "空" ) ;
		}else {
			log.debug("请求{},session解析来源header : {}" , request.getRequestURI() , sessionIds.get(0)) ;
		}
		// 解析cookie中的sessionId
		return sessionIds==null ? Collections.emptyList() : sessionIds ;
	}

	/**
	 * 是否为空的session信息
	 * @param sessionIds
	 * @return
	 */
	public boolean isEmpty(List<String> sessionIds){
		if( sessionIds == null ){
			return true ;
		}
		// 查找第一个不为空的sessionId
		for (String id : sessionIds) {
			if(UtilString.isNotBlank( id )){
				return false ;
			}
		}

		return true ;
	}

	@Override
	public void setSessionId(HttpServletRequest request, HttpServletResponse response,
							 String sessionId) {
		cookieHttpSessionIdResolver.setSessionId( request , response , sessionId ) ;
	}

	@Override
	public void expireSession(HttpServletRequest request, HttpServletResponse response) {

		cookieHttpSessionIdResolver.expireSession( request , response ) ;
	}

}
