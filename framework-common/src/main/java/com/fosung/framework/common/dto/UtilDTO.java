package com.fosung.framework.common.dto;

import com.fosung.framework.common.dto.support.DTOCallbackHandler;
import com.fosung.framework.common.dto.support.DTOCallbackHandlerAdaptor;
import com.fosung.framework.common.util.UtilCollection;
import com.fosung.framework.common.util.UtilReflection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 对dto对象的处理
 * @Author : liupeng
 * @Date : 2018-01-05
 * @Modified By
 */
@Slf4j
@ToString
public class UtilDTO {
	/**
	 * 批量转换对象为dto
	 * @param objects
	 * @param includeFields
	 * @param excludeFields
	 * @param dtoCallbackHandler
	 * @param <T>
	 * @return
	 */
	public static final <T> List<Map<String, Object>> toDTO(List<T> objects , Collection<String> includeFields , Collection<String> excludeFields ,
													  DTOCallbackHandler dtoCallbackHandler ){
		if( UtilCollection.isEmpty(objects) ){
			return null ;
		}

		// 列表包含元素的类型
		Class<?> itemClass = null;
		for (T object : objects) {
			if( object == null ){
				continue;
			}
			itemClass = object.getClass() ;
			break;
		}

		//如果没有指定包含的字段，默认使用所有的字段
		if( UtilCollection.isEmpty( includeFields ) ){
			//对map单独处理
			if( ClassUtils.isAssignable( itemClass , Map.class) ){
				includeFields = ((Map<String,?>)objects.get(0)).keySet() ;
			}else{
				includeFields = UtilReflection.getAllFieldNames( itemClass ) ;
			}
		}

		//排除不需要转换的字段
		if( excludeFields!=null ){
			includeFields.removeAll( excludeFields ) ;
		}

		List<Map<String, Object>> resultList = Lists.newArrayList() ;

		for (T object : objects) {
			//如果需要转换DTO字段为空，默认传输所有的字段
			Map<String, Object> dtoMap = Maps.newHashMapWithExpectedSize( includeFields.size() ) ;

			for (String includeField : includeFields) {
				try {
					dtoMap.put( includeField , PropertyUtils.getProperty( object , includeField ) ) ;
				} catch (Exception e) {
					log.error("获取类 {} 的属性 {} 错误" , object.getClass() , includeField ) ;
				}
			}

			if( dtoMap!=null && dtoMap.size()>0 ){
				resultList.add( dtoMap ) ;
			}
		}

		if( dtoCallbackHandler!=null ){
			// 如果是外部直接调用，转换单个对象，则也需要调用preHandle
			if( dtoCallbackHandler instanceof DTOCallbackHandlerAdaptor){
				((DTOCallbackHandlerAdaptor)dtoCallbackHandler).preHandle( resultList , itemClass) ;
			}

			// 对每一项进行处理
			for (Map<String, Object> dtoMap : resultList) {
				dtoCallbackHandler.doHandler( dtoMap , itemClass ) ;
			}

			// 如果是外部直接调用，转换单个对象，则处理完成之后需要调用 postHandle
			if( dtoCallbackHandler instanceof DTOCallbackHandlerAdaptor){
				((DTOCallbackHandlerAdaptor)dtoCallbackHandler).postHandle( resultList , itemClass ) ;
			}
		}

		return resultList ;
	}

	/**
	 * 转换对象为dto
	 * @param object
	 * @param includeFields
	 * @param excludeFields
	 * @param dtoCallbackHandler
	 * @param <T>
	 * @return
	 */
	public static final <T> Map<String, Object> toDTO(T object , Collection<String> includeFields , Collection<String> excludeFields ,
													  DTOCallbackHandler dtoCallbackHandler ){
		if(object==null){
			return null ;
		}

		List<Map<String, Object>> resultList = toDTO( Lists.newArrayList( object ) , includeFields , excludeFields , dtoCallbackHandler ) ;

		return resultList != null && resultList.size() > 0 ? resultList.get(0) : null ;
	}

	/**
	 * 转换对象为dto
	 * @param object
	 * @param excludeFields
	 * @param <T>
	 * @return
	 */
	public static final <T> Map<String, Object> toDTOExcludeFields(T object , Collection<String> excludeFields){
		return toDTO( object , null , excludeFields , null) ;
	}

	/**
	 * 转换对象为dto
	 * @param object
	 * @param excludeFields
	 * @param dtoCallbackHandler
	 * @param <T>
	 * @return
	 */
	public static final <T> Map<String, Object> toDTOExcludeFields(T object , Collection<String> excludeFields, DTOCallbackHandler dtoCallbackHandler){
		return toDTO( object , null , excludeFields , dtoCallbackHandler ) ;
	}

	/**
	 * 转换对象为dto
	 * @param objectList
	 * @param excludeFields
	 * @param <T>
	 * @return
	 */
	public static final <T> List<Map<String, Object>> toDTOExcludeFields(List<T> objectList , Collection<String> excludeFields){
		return toDTO( objectList , null , excludeFields , null) ;
	}

	/**
	 * 转换对象为dto
	 * @param objectList
	 * @param excludeFields
	 * @param dtoCallbackHandler
	 * @param <T>
	 * @return
	 */
	public static final <T> List<Map<String, Object>> toDTOExcludeFields(List<T> objectList , Collection<String> excludeFields, DTOCallbackHandler dtoCallbackHandler){
		return toDTO( objectList , null , excludeFields , dtoCallbackHandler ) ;
	}

	/**
	 * 转换对象为dto
	 * @param objectList
	 * @param dtoCallbackHandler
	 * @param <T>
	 * @return
	 */
	public static final <T> List<Map<String, Object>> toDTOExcludeFields(List<T> objectList , DTOCallbackHandler dtoCallbackHandler){
		return toDTO( objectList , null , null , dtoCallbackHandler ) ;
	}

	/**
	 * 转换对象为dto
	 * @param object
	 * @param objectFields
	 * @param <T>
	 * @return
	 */
	public static final <T> Map<String, Object> toDTO(T object , Collection<String> objectFields){
		return toDTO(object , objectFields , null , null ) ;
	}

	/**
	 * 转换对象为dto
	 * @param object
	 * @param objectFields
	 * @param dtoCallbackHandler
	 * @param <T>
	 * @return
	 */
	public static final <T> Map<String, Object> toDTO(T object , Collection<String> objectFields, DTOCallbackHandler dtoCallbackHandler){
		return toDTO(object , objectFields , null , dtoCallbackHandler ) ;
	}

	/**
	 * 转换对象为dto
	 * @param objectList
	 * @param objectFields
	 * @param <T>
	 * @return
	 */
	public static final <T> List<Map<String, Object>> toDTO(List<T> objectList , Collection<String> objectFields ){
		return toDTO(objectList , objectFields , null , null ) ;
	}

	/**
	 * 转换对象为dto
	 * @param objectList
	 * @param objectFields
	 * @param dtoCallbackHandler
	 * @param <T>
	 * @return
	 */
	public static final <T> List<Map<String, Object>> toDTO(List<T> objectList , Collection<String> objectFields , DTOCallbackHandler dtoCallbackHandler){

		return toDTO( objectList , objectFields , null , dtoCallbackHandler ) ;
	}

	/**
	 * 转换对象为dto
	 * @param objectList
	 * @param dtoCallbackHandler
	 * @param <T>
	 * @return
	 */
	public static final <T> List<Map<String, Object>> toDTO(List<T> objectList , DTOCallbackHandler dtoCallbackHandler){

		return toDTO( objectList , null , null , dtoCallbackHandler ) ;
	}

}
