package com.fosung.framework.common.id;

/**
 * 带有上下文的id生成
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public abstract class ContextIdGenerator<T> implements IdGenerator<T> {
    @Override
    public T getNextId() {
        return getNextId(null) ;
    }

    /**
     * 根据上下文生成id
     * @param object
     * @return
     */
    public abstract T getNextId( Object object ) ;
}
