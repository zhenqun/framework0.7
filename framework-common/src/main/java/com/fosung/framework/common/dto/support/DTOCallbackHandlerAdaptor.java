package com.fosung.framework.common.dto.support;

import java.util.List;
import java.util.Map;

/**
 * DTOCallbackHandler的适配器。添加一个dto处理完成的接口函数
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public abstract class DTOCallbackHandlerAdaptor implements DTOCallbackHandler {

	@Override
	public void doHandler(Map<String, Object> dtoMap , Class<?> itemClass) {

	}

	/**
	 * 在执行dto之前的处理函数
	 */
	public <T> void preHandle(List<Map<String, Object>> dtoMapList , Class<?> itemClass) {
	}

	/**
	 * 完成dto之后的处理函数
	 */
	public <T> void postHandle(List<Map<String, Object>> dtoMapList , Class<?> itemClass) {
	}
	

}
