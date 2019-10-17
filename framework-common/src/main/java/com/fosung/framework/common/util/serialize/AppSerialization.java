package com.fosung.framework.common.util.serialize;

import java.io.IOException;

/**
 * 序列化接口
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public interface AppSerialization {
	/**
	 * 序列化
	 * @param object
	 * @return
	 * @throws IOException
	 */
	byte[] serialize(Object object) throws IOException;

	/**
	 * 反序列化
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	Object deserialize(byte[] bytes) throws IOException;
}
