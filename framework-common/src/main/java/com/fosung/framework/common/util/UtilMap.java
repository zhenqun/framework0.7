package com.fosung.framework.common.util;

import com.fosung.framework.common.util.support.MapConverter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * map转换的处理
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilMap extends MapUtils {

    private static <T,K,V> Map<K,V> toMap(Iterable<T> list , MapConverter<T,K,V> mapConverter , String mapType ){
        if( list == null ){
            return null ;
        }

        //存储转换结果
        Map<K,V> resultMap = null ;
        if(StringUtils.equalsIgnoreCase( mapType , "linkedHashMap" )){
            resultMap = Maps.newLinkedHashMap() ;
        }else{
            resultMap = Maps.newHashMap() ;
        }

        //执行迭代，转换map
        Iterator<T> iterator = list.iterator() ;
        while (iterator.hasNext()){
            T item = iterator.next() ;

            //分别获取key和value
            K key = mapConverter.getKey( item ) ;

            V value = mapConverter.getValue( item ) ;

            resultMap.put( key , value) ;
        }

        return resultMap ;
    }

    /**
     * 通过迭代获取一个Map结合，key和value都是单个对象
     * @param list
     * @param mapConverter
     * @param <T>
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T,K,V> Map<K,V> toMap( Iterable<T> list , MapConverter<T,K,V> mapConverter ){

        return toMap(list , mapConverter , "map") ;
    }

    /**
     * 通过迭代获取一个map集合，value值为一个Set
     * @param list
     * @param mapConverter
     * @param <T>
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T,K,V> Map<K,Set<V>> toSetValueMap(Iterable<T> list , MapConverter<T,K,V> mapConverter ){
        if( list == null ){
            return null ;
        }

        //存储转换结果
        Map<K,Set<V>> resultMap = Maps.newHashMap() ;

        //执行迭代，转换map
        Iterator<T> iterator = list.iterator() ;
        while (iterator.hasNext()){
            T item = iterator.next() ;

            //分别获取key和value
            K key = mapConverter.getKey( item ) ;

            //如果key对应的value为空，则初始化为set
            if( resultMap.get(key)==null ){
                resultMap.put(key , Sets.newHashSet()) ;
            }

            V value = mapConverter.getValue( item ) ;
            if( value!=null ){
                resultMap.get(key).add( value ) ;
            }
        }

        return resultMap ;
    }

    /**
     * 通过迭代获取一个map集合，value值为一个List
     * @param list
     * @param mapConverter
     * @param <T>
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T,K,V> Map<K,List<V>> toListValueMap(Iterable<T> list , MapConverter<T,K,V> mapConverter ){
        if( list == null ){
            return null ;
        }

        //存储转换结果
        Map<K,List<V>> resultMap = Maps.newHashMap() ;

        //执行迭代，转换map
        Iterator<T> iterator = list.iterator() ;
        while (iterator.hasNext()){
            T item = iterator.next() ;

            //分别获取key和value
            K key = mapConverter.getKey( item ) ;

            //如果key对应的value为空，则初始化为set
            if( resultMap.get(key)==null ){
                resultMap.put(key , Lists.newArrayList()) ;
            }

            V value = mapConverter.getValue( item ) ;
            if( value!=null ){
                resultMap.get(key).add( value ) ;
            }
        }

        return resultMap ;
    }


    /**
     * map转换为environment对象
     * @param source
     * @param <V>
     * @return
     */
    public static  <V> Environment toEnvironment(Map<String,V> source ){
        ConfigurableMap configurableMap = new ConfigurableMap( source ) ;

        return configurableMap ;
    }

    private static class ConfigurableMap extends AbstractEnvironment {

        public static final String CUSTOM_PROPERTY_SOURCE_NAME = "customEnvironment";

        public <V> ConfigurableMap(Map<String,V> source){

            if( source!=null ){
                Map<String,Object> propertySource = Maps.newHashMap() ;
                for (Map.Entry<String, V> item : source.entrySet()) {
                    propertySource.put( item.getKey() , item.getValue() );
                }

                this.getPropertySources().addLast( new MapPropertySource( CUSTOM_PROPERTY_SOURCE_NAME , propertySource ));
            }
        }

    }

}
