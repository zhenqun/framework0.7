package com.fosung.framework.dao.support.service.handler;

import com.fosung.framework.common.support.dao.handler.AppBaseDataHandlerAdaptor;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.reflections.ReflectionUtils;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对更新字段进行过滤，只允许实体字段的可更新
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@Slf4j
public class AppBaseDataHandlerWithUpdateFields extends AppBaseDataHandlerAdaptor {

    /**
     * 系统不允许更新的字段
     */
    public static final Set<String> NOT_ALLOWED_FIELDS  = Sets.newHashSet( "id" , "serialVersionUID" ,
            "createUserId" , "createDatetime" ) ;

    private Map<String,Set<String>> entityFieldsMap = Maps.newHashMap() ;

    @Override
    public boolean support(Class<?> entityClass) {
        return true;
    }

    public <T> void initEntityFields( T entity ){
        Set<String>  entityFields = Sets.newHashSet() ;
        for (Field field : ReflectionUtils.getAllFields(entity.getClass()) ) {
            //过滤掉需要映射的jpa字段
            Transient fieldTransient = field.getAnnotation(Transient.class) ;
            if(fieldTransient!=null){
                continue ;
            }

            entityFields.add(field.getName()) ;
        }
        entityFieldsMap.put( entity.getClass().getName() , entityFields ) ;
    }

    /**
     * 获取实体字段的名称
     * @param entity
     * @param <T>
     * @return
     */
    public <T> Set<String> getFieldNames( T entity ){
        if( ! entityFieldsMap.containsKey( entity.getClass().getName() ) ){
            initEntityFields( entity ) ;
        }
        return entityFieldsMap.get( entity.getClass().getName() ) ;
    }

    @Override
    public <T> T preUpdateHandler(T entity, Collection<String> updateFields) {
        entity = super.preUpdateHandler(entity, updateFields);

        // 获取实体字段
        Set<String> entityFields = getFieldNames( entity ) ;

        //检查更新实体的属性，如果不是实体的属性，则自动移除
        if( updateFields!=null ){
            List<String> removeUpdateFields = Lists.newArrayList() ;
            for (String updateField : updateFields) {
                if( entityFields.contains(updateField)){
                    continue;
                }
                removeUpdateFields.add( updateField ) ;
                log.info("更新之前移除 {} 中不包含的属性 {}" , entity.getClass().getName() , updateField) ;
            }
            updateFields.removeAll( removeUpdateFields ) ;
        }

        if( updateFields!=null ){
            // 移除所有不允许更新的字段
            updateFields.removeAll( NOT_ALLOWED_FIELDS ) ;
        }

        log.info( "{} 更新的字段包括: {}" , entity.getClass().getName() , Joiner.on(",").join( updateFields ) );

        return entity ;
    }
}
