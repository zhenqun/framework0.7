package com.fosung.framework.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 集合操作类
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
public abstract class UtilCollection extends CollectionUtils {

    /**
     * 导出列表中可以列表中可以迭代的对象
     * @param iterable 可迭代的对象
     * @param fieldName 字段名称
     * @param fieldValueType 字段值类型
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T , E> Set<T> extractToSet(Iterable<E> iterable , String fieldName , Class<T> fieldValueType ){
        List<T> result = extractToList( iterable , fieldName , fieldValueType ) ;

        if(result!=null){
            Set<T> resultSet = Sets.newHashSet() ;
            resultSet.addAll( result ) ;
            return resultSet ;
        }

        return null ;
    }


    /**
     * 导出列表中可以列表中可以迭代的对象
     * @param iterable 可迭代的对象
     * @param fieldName 字段名称
     * @param fieldValueType 字段值类型
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T , E> List<T> extractToList(Iterable<E> iterable , String fieldName , Class<T> fieldValueType ){
        if (iterable==null || StringUtils.isBlank(fieldName) || fieldValueType==null){
            return null ;
        }
        //获取迭代对象
        Iterator<E> iterator = iterable.iterator() ;

        //创建查询结果
        List<T> result = Lists.newArrayList() ;

        //遍历迭代
        while (iterator.hasNext()){
            E item = iterator.next() ;
            Object value = null ;
            //如果value是一个map
            if( ClassUtils.isAssignable( item.getClass() , Map.class ) ){
                value = ((Map)item).get( fieldName ) ;
            }else{
                try {
                    Map<String, Object> itemMap = PropertyUtils.describe( item ) ;
                    if(itemMap!=null){
                        value = itemMap.get( fieldName ) ;
                    }
                }catch (Exception e){
                    log.error( e.getMessage() ) ;
                }
            }

            //将value加入到查询结果
            if( value!=null && ClassUtils.isAssignable( value.getClass() , fieldValueType ) ){
                result.add( (T)value ) ;
            }
        }

        return result ;
    }

}
