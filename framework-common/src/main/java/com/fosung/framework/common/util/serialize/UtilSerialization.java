package com.fosung.framework.common.util.serialize;


import com.fosung.framework.common.util.serialize.support.AppSerializationByHessian;
import com.fosung.framework.common.util.serialize.support.AppSerializationByJava;

import java.util.HashMap;
import java.util.Map;
/**
 * 序列化对象工厂，根据类型获取不同的序列化对象
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public class UtilSerialization {
	/**
	 * 设置序列化对象
	 */
	private static final Map<AppSerializationType, AppSerialization> serializationMap = new HashMap<AppSerializationType, AppSerialization>(){
		{
			put(AppSerializationType.JAVA , new AppSerializationByJava()) ;
			put(AppSerializationType.HESSIAN , new AppSerializationByHessian()) ;
		}
	};
	
	public static AppSerialization getSerialization(AppSerializationType type){
		AppSerialization appSerialization = serializationMap.get(type) ;
		return appSerialization;
	}
	
}
