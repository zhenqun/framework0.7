package com.fosung.framework.common.dto;

import com.fosung.framework.common.dto.support.DTOCallbackHandler;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * 对待转换的实体中的数字进行转换，默认的数字格式为"##.00"
 */
public class DTOCallbackHandlerWithNumber implements DTOCallbackHandler {

    /**
     * 默认的数字格式
     */
    public static final  String DEFAULT_NUMBER_FORMAT_PATTERN = "#0.00" ;

    /**
     * 默认的数字转换器
     */
    public static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat(DEFAULT_NUMBER_FORMAT_PATTERN) ;

    private Map<String , DecimalFormat> propertyFormatMap = Maps.newHashMap() ;

    /**
     * 添加需要在进行转换的数字格式，数字格式使用默认格式
     * @param propertyName
     */
    public void addNumberProperty(String propertyName){
        addNumberProperty(propertyName , DEFAULT_NUMBER_FORMAT_PATTERN) ;
    }

    /**
     * 添加需要在进行转换的数字格式，数字格式使用自定义的格式
     * @param propertyName
     * @param format
     */
    public void addNumberProperty(String propertyName , String format){
        if(StringUtils.isBlank(propertyName)){
            return ;
        }
        Assert.isTrue(StringUtils.isNotBlank(format) , "格式化数字的格式不能为空") ;

        if(DEFAULT_NUMBER_FORMAT_PATTERN.equalsIgnoreCase(format)){
            propertyFormatMap.put(propertyName , DEFAULT_DECIMAL_FORMAT) ;
        }else{
            propertyFormatMap.put(propertyName , new DecimalFormat(format)) ;
        }

    }

    /**
     * 进行数字转换的处理
     * @param dtoMap
     */
    @Override
    public void doHandler(Map<String, Object> dtoMap , Class<?> itemClass) {

        //遍历需要数字格式化的字段，进行格式化 AppJpaBaseEntity
        for (Map.Entry<String, DecimalFormat> propertyFormat : propertyFormatMap.entrySet()) {
            if( dtoMap.get(propertyFormat.getKey()) == null){
                continue;
            }

            String value = propertyFormat.getValue().format( dtoMap.get(propertyFormat.getKey()) ) ;

            dtoMap.put(propertyFormat.getKey() , value) ;
        }

    }

}
