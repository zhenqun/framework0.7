package com.fosung.framework.common.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 使用ali json工具类，简单的配置和封装，实现JSON String<->Java Object的Mapper.
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public class JsonMapper extends JSON{
	
	static{
		JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteMapNullValue.getMask() ;
	}

}
