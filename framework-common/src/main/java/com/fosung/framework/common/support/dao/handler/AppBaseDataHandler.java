package com.fosung.framework.common.support.dao.handler;

import org.springframework.core.Ordered;

import java.util.Collection;

/**
 * 实体及相关逻辑 的处理接口类
 * @Author : liupeng
 * @Date : 2018-11-11
 * @Modified By
 */
public interface AppBaseDataHandler extends Ordered {

    /**
     * 是否支持实体类的处理
     * @param entityClass
     * @return
     */
    boolean support(Class<?> entityClass) ;

    /**
     * handler顺序，默认为0，最高优先级： Integer.MIN_VALUE ， 最低优先级： Integer.MAX_VALUE
     * @return
     */
    @Override
    default int getOrder(){
        return 0 ;
    }

    /**
     * 保存之前执行的操作
     * @param entity
     */
    default <T> T preSaveHandler(T entity){
        return entity ;
    }

    /**
     * 保存之后执行的操作
     * @param entity
     */
    default <T> T postSaveHandler(T entity) {
        return entity ;
    }

    /**
     * 更新之前执行的操作
     * @param entity
     * @param updateFields
     */
    default <T> T preUpdateHandler(T entity, Collection<String> updateFields) {
        return entity ;
    }

    /**
     * 更新之后执行的操作
     * @param entity
     * @param updateFields
     */
    default <T> T postUpdateHandler(T entity, Collection<String> updateFields) {
        return entity ;
    }

    /**
     * 删除前执行的操作
     * @param id
     * @param domainClass 领域类
     */
    default <ID> void preDeleteHandler(ID id, Class<?> domainClass){

    }

    /**
     * 删除后执行的操作
     * @param id
     * @param domainClass 领域类
     */
    default <ID> void postDeleteHandler(ID id, Class<?> domainClass) {

    }

}
