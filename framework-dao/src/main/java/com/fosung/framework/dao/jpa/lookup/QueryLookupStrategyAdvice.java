package com.fosung.framework.dao.jpa.lookup;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;

import java.lang.reflect.Method;

/**
 * 基于不同类型的查询策略
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
public abstract class QueryLookupStrategyAdvice implements QueryLookupStrategy{

    protected BeanFactory beanFactory ;

    public QueryLookupStrategyAdvice(BeanFactory beanFactory){
        this.beanFactory = beanFactory ;
    }

    /**
     * 获取名称
     * @return
     */
    abstract String getName() ;

    /**
     * 是否允许
     * @param method
     * @param metadata
     * @return
     */
    abstract boolean isEnabled( Method method , RepositoryMetadata metadata ) ;

}
