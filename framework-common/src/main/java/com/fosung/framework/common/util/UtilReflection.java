package com.fosung.framework.common.util;

import com.google.common.collect.Sets;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * 反射操作类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilReflection extends ReflectionUtils {

    /**
     * 获取所有声明字段的名称
     * @param type
     * @return
     */
    public static Set<String> getAllFieldNames(Class<?> type){
        Set<Field> fields = getAllFields( type ,null ) ;
        if( UtilCollection.isEmpty(fields) ){
            return Sets.newHashSetWithExpectedSize(0) ;
        }

        Set<String> fieldNames = Sets.newHashSetWithExpectedSize( fields.size() ) ;

        for (Field field : fields) {
            //不对静态字段进行处理
            if( Modifier.isStatic( field.getModifiers() ) ){
                continue ;
            }
            fieldNames.add( field.getName() ) ;
        }

        return fieldNames ;
    }

}
