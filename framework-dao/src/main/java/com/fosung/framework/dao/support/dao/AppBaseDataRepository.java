package com.fosung.framework.dao.support.dao;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 基本spring data接口，包含基本的持久化操作
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@NoRepositoryBean
public interface AppBaseDataRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T,ID> {
    /**
     * 更新实体对象
     * @param entity
     * @param updateFieldsName
     * @return
     */
    T update(T entity, Collection<String> updateFieldsName) ;

    /**
     * 批量更新实体对象
     * @param entityList
     * @param updateFieldsName
     * @return
     */
    List<T> update(List<T> entityList, Collection<String> updateFieldsName) ;

    /**
     * 获取进行操作的领域类
     * @return
     */
    Class<T> getDomainClass() ;

}
