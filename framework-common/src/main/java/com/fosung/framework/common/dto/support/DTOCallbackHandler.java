package com.fosung.framework.common.dto.support;

import java.util.Map;

/**
 * DTO对象的回调函数处理
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public interface DTOCallbackHandler{
	/**
	 * 对dto对象的处理
	 * @param dtoMap
	 * @param itemClass
	 */
	void doHandler(Map<String, Object> dtoMap , Class<?> itemClass) ;
}