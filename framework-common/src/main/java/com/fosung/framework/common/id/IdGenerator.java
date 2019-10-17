package com.fosung.framework.common.id;

/**
 * id生成工具
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public interface IdGenerator<T> {

    /**
     * 生成id
     * @return
     */
    T getNextId() ;

}
