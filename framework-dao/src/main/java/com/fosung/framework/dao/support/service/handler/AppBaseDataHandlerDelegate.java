package com.fosung.framework.dao.support.service.handler;

import com.fosung.framework.common.support.dao.handler.AppBaseDataHandler;
import com.fosung.framework.common.support.dao.handler.AppBaseDataHandlerAdaptor;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Collection;
import java.util.List;

/**
 * 实体持久化代理钩子类
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@Slf4j
public class AppBaseDataHandlerDelegate extends AppBaseDataHandlerAdaptor {

    private List<AppBaseDataHandler> appBaseDataHandlers = Lists.newArrayList();

    /**
     * 添加对实体处理的接口 handler
     * @param appBaseDataHandler
     */
    public void addAppBaseDataHandler( AppBaseDataHandler appBaseDataHandler ){
        if( appBaseDataHandler==null || appBaseDataHandlers.contains( appBaseDataHandler ) ){
            return ;
        }
        // 加入处理队列
        appBaseDataHandlers.add( appBaseDataHandler ) ;

        // 对 hanlder 按照 Order 接口 进行排序
        AnnotationAwareOrderComparator.sort( appBaseDataHandlers ) ;
    }

    @Override
    public boolean support(Class<?> entityClass) {
        return true;
    }

    @Override
    public <T> T preSaveHandler(T entity) {
        for (AppBaseDataHandler appBaseDataHandler : appBaseDataHandlers) {
            if( ! appBaseDataHandler.support( entity.getClass() ) ){
                continue;
            }
            entity = appBaseDataHandler.preSaveHandler( entity ) ;
        }
        return entity ;
    }

    @Override
    public <T> T postSaveHandler(T entity) {
        for (AppBaseDataHandler appBaseDataHandler : appBaseDataHandlers) {
            if( ! appBaseDataHandler.support( entity.getClass() ) ){
                continue;
            }
            entity = appBaseDataHandler.postSaveHandler( entity ) ;
        }
        return entity ;
    }

    @Override
    public <T> T preUpdateHandler(T entity, Collection<String> updateFields) {
        for (AppBaseDataHandler appBaseDataHandler : appBaseDataHandlers) {
            if( ! appBaseDataHandler.support( entity.getClass() ) ){
                continue;
            }
            entity = appBaseDataHandler.preUpdateHandler( entity , updateFields ) ;
        }
        return entity ;
    }

    @Override
    public <T> T postUpdateHandler(T entity, Collection<String> updateFields) {
        for (AppBaseDataHandler appBaseDataHandler : appBaseDataHandlers) {
            if( ! appBaseDataHandler.support( entity.getClass() ) ){
                continue;
            }
            entity = appBaseDataHandler.postUpdateHandler( entity , updateFields ) ;
        }

        return entity ;
    }

    @Override
    public <ID> void preDeleteHandler(ID id, Class<?> domainClass) {
        for (AppBaseDataHandler appBaseDataHandler : appBaseDataHandlers) {
            if( ! appBaseDataHandler.support( domainClass ) ){
                continue;
            }
            appBaseDataHandler.preDeleteHandler( id , domainClass);
        }
    }

    @Override
    public <ID> void postDeleteHandler(ID id , Class<?> domainClass) {
        for (AppBaseDataHandler appBaseDataHandler : appBaseDataHandlers) {
            if( ! appBaseDataHandler.support( domainClass ) ){
                continue;
            }
            appBaseDataHandler.postDeleteHandler( id , domainClass);
        }
    }

}
