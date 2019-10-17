package com.fosung.framework.common.util.serialize.support;

import com.fosung.framework.common.util.serialize.AppSerialization;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class AppSerializationByJava implements AppSerialization {

	@Override
	public byte[] serialize(Object object) throws IOException {
		byte[] result = null;
		
		if (object == null) {
			return new byte[0];
		}
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
			try  {
				if (!(object instanceof Serializable)) {
					throw new IllegalArgumentException(AppSerializationByJava.class.getSimpleName() + " requires a Serializable payload " +
							"but received an object of type [" + object.getClass().getName() + "]");
				}
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
				objectOutputStream.writeObject(object);
				objectOutputStream.flush();
				result =  byteStream.toByteArray();
			}
			catch (Throwable ex) {
				throw new Exception("Failed to serialize", ex);
			}
		} catch (Exception ex) {
			log.error("Failed to serialize" , ex);
		}
		return result;
	}

	@Override
	public Object deserialize(byte[] bytes) throws IOException {
		Object result = null;
		
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		try {
			ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
			try {
				ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);
				try {
					result = objectInputStream.readObject();
				}
				catch (ClassNotFoundException ex) {
					throw new Exception("Failed to deserialize object type", ex);
				}
			}
			catch (Throwable ex) {
				throw new Exception("Failed to deserialize", ex);
			}
		} catch (Exception e) {
			log.error("Failed to deserialize",e);
		}
		return result;
	}

}
