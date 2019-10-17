package com.fosung.framework.common.dto;

import com.fosung.framework.common.dto.support.DTOCallbackHandler;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 清理系统属性的字段，不允许敏感字段外放
 * @Author : liupeng
 * @Date : 2018-11-02
 * @Modified By
 */
public class DTOCallbackHandlerClearBaseEntityFields implements DTOCallbackHandler {

    /**
     * 在转换为dto对象时，默认忽略的字段
     */
    public static final List<String> DEFAULT_IGNORE_FIELDS = Lists.newArrayList() ;

    static {
        DEFAULT_IGNORE_FIELDS.addAll(Arrays.asList(
            "createUserId" , "lastUpdateUserId" , "serialVersionUID"
        )) ;
    }

    /**
     * 移除忽略的字段
     * @param dtoMap
     */
    @Override
    public void doHandler(Map<String, Object> dtoMap , Class<?> itemClass) {
        if( dtoMap==null ){
            return ;
        }

        for (String defaultIgnoreField : DEFAULT_IGNORE_FIELDS) {
            dtoMap.remove( defaultIgnoreField ) ;
        }

    }

}
