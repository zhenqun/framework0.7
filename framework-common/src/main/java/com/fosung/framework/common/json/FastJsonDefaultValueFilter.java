package com.fosung.framework.common.json;

import com.alibaba.fastjson.serializer.ValueFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 对数值类型转换成String传输
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
public class FastJsonDefaultValueFilter implements ValueFilter {
    @Override
    public Object process(Object object, String name, Object value) {
        if( value!=null ){
            //数字全部转为字符串，日期默认使用long格式的time
            if( value instanceof Number ){
                return value.toString() ;
            }else if( value instanceof Date){
                return ((Date)value).getTime() ;
            }
        }
        return value ;
    }

}
