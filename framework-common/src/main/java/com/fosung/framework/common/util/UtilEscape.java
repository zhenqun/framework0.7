package com.fosung.framework.common.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 避免sql注入转义操作类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilEscape extends StringEscapeUtils {
	
	public static final String escapeSQL(String input) {
		if(StringUtils.isBlank(input)){
			return "" ;
		}
		
		input = input.replaceAll("[%_$]", "/$0") ;
		
		input = input.replaceAll("'", "''") ;
		
		return input ;
    }
	
}
