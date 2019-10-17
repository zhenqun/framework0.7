package com.fosung.framework.web.http;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.dto.DTOCallbackHandlerDelegate;
import com.fosung.framework.common.dto.DTOCallbackHandlerWithNumber;
import com.fosung.framework.common.dto.support.DTOCallbackHandler;
import com.fosung.framework.common.exception.AppWebException;
import com.fosung.framework.common.id.snowflake.AppIDGenerator;
import com.fosung.framework.common.secure.auth.AppUserDetails;
import com.fosung.framework.common.secure.auth.AppUserDetailsService;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.common.interceptor.support.AppServletContextHolder;
import com.fosung.framework.web.util.UtilWeb;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * controller请求基础类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
public abstract class AppIBaseController extends UtilWeb implements ApplicationContextAware , ResourceLoaderAware , InitializingBean {

	/**
	 * 分页默认开始页号
	 */
	public static final int DEFAULT_PAGE_START_NUM = 0 ;
	/**
	 * 分页默认大小
	 */
	public static final int DEFAULT_PAGE_SIZE_NUM = 10 ;

	/**
	 * 查询参数名称默认的开始符号
	 */
	public static final String DEFAULT_SEARCH_PREFIX = "search_" ;

	private static final AppIDGenerator idGenerator = new AppIDGenerator() ;

	@Autowired
	protected AppProperties appProperties ;

	@Autowired
	protected StringRedisTemplate stringRedisTemplate ;

	@Autowired(required = false)
	protected AppUserDetailsService appUserDetailsService ;

	protected ResourceLoader resourceLoader ;

	protected ApplicationContext applicationContext ;

	protected DTOCallbackHandlerDelegate dtoCallbackHandlerDelegate = new DTOCallbackHandlerDelegate() ;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader ;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext ;
	}

	/**
	 * 获取分布式id
	 * @return
	 */
	public Long getNextId(){
		return idGenerator.getNextId() ;
	}

	/**
	 * 获取登录认证后的用户信息
	 * @return
	 */
	public AppUserDetails getLoginAppUserDetails(){
		AppUserDetails appUserDetails = appUserDetailsService != null ?
				appUserDetailsService.getAppUserDetails() : null ;

		if( appUserDetails==null || appUserDetails.getUserId()==null ){
			throw new AppWebException( HttpStatus.UNAUTHORIZED.value() , "当前用户未登录或登录超时" ) ;
		}

		return appUserDetails ;
	}

	/**
	 * 获取登录用户的id
	 * @return
	 */
	public Long getLoginUserId(Class<Long> targetClass){
		if( getLoginAppUserDetails().getUserId() == null ){
			return null ;
		}
		return Longs.tryParse( getLoginAppUserDetails().getUserId().toString() ) ;
	}

	/**
	 * 获取登录用户的id
	 * @return
	 */
	public String getLoginUserId(){
		if( getLoginAppUserDetails().getUserId() == null ){
			return null ;
		}
		return getLoginAppUserDetails().getUserId().toString() ;
	}

	/**
	 * 获取登录用户的角色
	 * @return
	 */
	public Set<String> getLoginUserRoles(){
		return getLoginAppUserDetails().getUserRoleCodes() ;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, DTOCallbackHandler> dtoCallbackHandlerMap = applicationContext.getBeansOfType( DTOCallbackHandler.class ) ;
		if( dtoCallbackHandlerMap!=null ){
			log.debug("找到{}个自定义的 DTOCallbackHandler" , dtoCallbackHandlerMap.size() );
			dtoCallbackHandlerDelegate.addDTOCallbackHandlerTask( dtoCallbackHandlerMap.values().toArray( new DTOCallbackHandler[]{} ) );
		}
	}

	/**
	 * 添加类的数字属性和格式化方式的映射
	 * @param property
	 * @param format
	 */
	public void addNumberProperty(String property , String format){
		this.dtoCallbackHandlerDelegate.getDTOCallbackHandler( DTOCallbackHandlerWithNumber.class )
				.addNumberProperty(property , format) ;
	}
	
	/**
	 * 初始化前端模型视图中的分页参数
	 * @param mv
	 * @param page
	 */
	public void initPageParam(ModelAndView mv , Page<?> page){
//		Map<String, Object> pageParamMap = new HashMap<String, Object>() ;
//		initPageParam(pageParamMap, page);
		//添加分页参数
		mv.addAllObjects(ResponseParam.success().pageParam( page )) ;
	}

	/**
	 * 获取当前请求的HttpServletRequest对象对象
	 * @return
	 */
	public HttpServletRequest getHttpServletRequest() {
		return AppServletContextHolder.getHttpServletRequest() ;
	}
	
	/**
	 * 获取当前请求的HttpServletResponse对象对象
	 * @return
	 */
	public HttpServletResponse getHttpServletResponse() {
		return AppServletContextHolder.getHttpServletResponse() ;
	}

	/**
	 * 由于在生产环境经过反向代理转发之后，请求可能会都被转化为http协议，封装一个方法统一获取请求schema
	 * @return
	 */
	public String getRequestSchema(){
		//从nginx自定义的head中判断是http或https请求
		String schema = getHttpServletRequest().getScheme() ;
		return StringUtils.equalsIgnoreCase( schema , "https" ) ? "https" : "http" ;
	}

	/**
	 * 获取dto处理对象
	 * @param dtoCallbackHandler
	 * @return
	 */
	public DTOCallbackHandler getDTOCallbackHandlerDelegate(DTOCallbackHandler dtoCallbackHandler){
		return getDTOCallbackHandlerDelegate(dtoCallbackHandler , false) ;
	}

	/**
	 * 获取dto处理对象
	 * @param dtoCallbackHandler
	 * @return
	 */
	public DTOCallbackHandler getDTOCallbackHandlerProxy(DTOCallbackHandler dtoCallbackHandler){
		return getDTOCallbackHandlerDelegate( dtoCallbackHandler ) ;
	}

	public DTOCallbackHandler getDTOCallbackHandlerProxy(DTOCallbackHandler dtoCallbackHandler , boolean executeTargetDTOCallbackHandlerFirst){
		return getDTOCallbackHandlerDelegate( dtoCallbackHandler , executeTargetDTOCallbackHandlerFirst ) ;
	}

	/**
	 * 获取dto回调处理类
	 * @param dtoCallbackHandler
	 * @param executeTargetDTOCallbackHandlerFirst 在其它dtoCallbackHandler执行之前首先执行被代理的dtoCallbackHandler
	 * @return
	 */
	public DTOCallbackHandler getDTOCallbackHandlerDelegate(DTOCallbackHandler dtoCallbackHandler , boolean executeTargetDTOCallbackHandlerFirst){

		//重新构建一个新的DTOCallbackHandler，不使用ThreadLocal对象存储hanlder，避免在嵌套调用时出现的bug
		DTOCallbackHandlerDelegate newDTOCallbackHandlerDelegate = new DTOCallbackHandlerDelegate() ;
		//设置新的处理链
		newDTOCallbackHandlerDelegate.getDtoCallbackHandlers().addAll(
				this.dtoCallbackHandlerDelegate.getDtoCallbackHandlers() ) ;

		newDTOCallbackHandlerDelegate.setTargetDTOCallbackHandler( dtoCallbackHandler ) ;

		newDTOCallbackHandlerDelegate.setExecuteTargetDTOCallbackHandlerFirst(
				executeTargetDTOCallbackHandlerFirst ) ;

		return newDTOCallbackHandlerDelegate;
	}

	/**
	 * 将字符串转换long类型的数组
	 *
	 * @param ids
	 * @return
	 */
	public Long[] toLongIds(String ids) {
		if (UtilString.isBlank(ids)) {
			return null;
		}
		List<String> stringIds = Splitter.on(",").splitToList(ids);

		List<Long> longIds = Lists.newArrayListWithCapacity(stringIds.size());

		for (String stringId : stringIds) {
			Long longId = Longs.tryParse(stringId);
			if (longId == null) {
				continue;
			}
			longIds.add(longId);
		}

		return longIds.toArray(new Long[]{});
	}


}
