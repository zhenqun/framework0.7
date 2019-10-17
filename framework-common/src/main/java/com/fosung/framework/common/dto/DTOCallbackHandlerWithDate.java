package com.fosung.framework.common.dto;


import com.fosung.framework.common.dto.support.DTOCallbackHandler;
import com.fosung.framework.common.util.UtilString;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

/**
 * 对日期格式进行转化
 * @Author : liupeng
 * @Date : 2018/8/14 17:57
 * @Modified By
 */
@Slf4j
public class DTOCallbackHandlerWithDate implements DTOCallbackHandler {

    /**
     * 判断值是否为Date类型
     *
     * @param value
     * @return
     */
    public boolean match(Object value) {
        return value != null && value instanceof Date;
    }

    /**
     * 进行日期格式转换的处理
     * @param dtoMap
     */
    @Override
    public void doHandler(Map<String, Object> dtoMap , Class<?> itemClass) {
        //判断map是否为空
        if (dtoMap == null) {
            return;
        }

        Map<String,Object> dateMap = Maps.newHashMap() ;

        dtoMap.entrySet().stream().filter( item->UtilString.isNotBlank(item.getKey()) && match(item.getValue()))
            .forEach( item -> {
                try {
                    Field dateField = ReflectionUtils.findRequiredField( itemClass , item.getKey() ) ;
                    DateTimeFormat dateTimeFormat = dateField.getAnnotation(DateTimeFormat.class) ;
                    if( dateTimeFormat != null ){
                        dateMap.put( item.getKey() , new DateTime(item.getValue()).toString( dateTimeFormat.pattern() ) ) ;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } );

        dtoMap.putAll( dateMap ) ;
    }

}
