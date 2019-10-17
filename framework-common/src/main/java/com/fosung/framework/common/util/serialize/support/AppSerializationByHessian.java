package com.fosung.framework.common.util.serialize.support;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.fosung.framework.common.util.serialize.AppSerialization;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

@Slf4j
public class AppSerializationByHessian implements AppSerialization {

	@Override
	public byte[] serialize(Object object) throws IOException {
		//如果对象为空或未实现序列化接口，不进行转换
		if(object==null){
			return null ;
		}
		if(!Serializable.class.isAssignableFrom(object.getClass())){
			throw new IllegalArgumentException(object.getClass()+"没有实现 "+Serializable.class+" 接口。") ;
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Hessian2Output hessian2Output = new Hessian2Output(os) ;
		try {
			hessian2Output.writeObject(object);
			//刷新
			hessian2Output.flush();
			return os.toByteArray() ;
		} catch (IOException e) {
			e.printStackTrace();
			return null ;
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws IOException {
		if(bytes == null || bytes.length == 0){
			return null;
		}

		Hessian2Input hessian2Input = new Hessian2Input(new ByteArrayInputStream(bytes)) ;
		try {
			return hessian2Input.readObject() ;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
