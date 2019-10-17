package com.fosung.framework.common.support.dao.handler;

import java.util.Collection;

/**
 * 实体及相关逻辑 的处理适配类
 * @Author : liupeng
 * @Date : 2018-11-11
 * @Modified By
 */
public abstract class AppBaseDataHandlerAdaptor implements AppBaseDataHandler {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public <T> T preSaveHandler(T entity) {
        return entity;
    }

    @Override
    public <T> T postSaveHandler(T entity) {
        return entity;
    }

    @Override
    public <T> T preUpdateHandler(T entity, Collection<String> updateFields) {
        return entity;
    }

    @Override
    public <T> T postUpdateHandler(T entity, Collection<String> updateFields) {
        return entity;
    }

    @Override
    public <ID> void preDeleteHandler(ID id , Class<?> domainClass) {

    }

    @Override
    public <ID> void postDeleteHandler(ID id , Class<?> domainClass) {

    }
}
