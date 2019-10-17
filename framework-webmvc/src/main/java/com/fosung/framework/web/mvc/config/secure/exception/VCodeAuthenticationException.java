package com.fosung.framework.web.mvc.config.secure.exception;

import com.fosung.framework.common.exception.AppException;

/**
 * 验证码异常
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public class VCodeAuthenticationException extends AppException {
	private static final long serialVersionUID = 1L;

	public VCodeAuthenticationException(){
		super("验证码错误。") ;
	}
	
	public VCodeAuthenticationException(String message){
		super(message) ;
	}
	
}
