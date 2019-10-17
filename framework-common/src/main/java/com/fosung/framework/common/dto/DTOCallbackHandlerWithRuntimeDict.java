package com.fosung.framework.common.dto;


import com.fosung.framework.common.dto.support.DTOCallbackHandler;
import com.fosung.framework.common.support.AppRuntimeDict;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 对待转换的实体进行运行时的字典项的转换
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Setter
@Getter
@Slf4j
public class DTOCallbackHandlerWithRuntimeDict implements DTOCallbackHandler {

    //转换后字典项的后缀
    private String postfix = "_dict";

    /**
     * 进行字典项转换的处理
     *
     * @param dtoMap
     */
    @Override
    public void doHandler(Map<String, Object> dtoMap , Class<?> itemClass) {
        if (dtoMap == null) {
            return;
        }
        // 临时存储转换后的字典项
        Map<String, String> dictDto = Maps.newHashMap();

        for (Map.Entry<String, Object> item : dtoMap.entrySet()) {
            Object dictObject = item.getValue() ;
            if( dictObject==null || !(dictObject instanceof AppRuntimeDict) ){
                continue;
            }
            AppRuntimeDict appRuntimeDict = (AppRuntimeDict) dictObject ;

            dictDto.put( item.getKey() + postfix , appRuntimeDict.getRemark() ) ;
        }

        dtoMap.putAll(dictDto);
    }

}
