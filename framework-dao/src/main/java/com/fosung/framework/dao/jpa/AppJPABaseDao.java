package com.fosung.framework.dao.jpa;

import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * jpa持久化基础类
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@NoRepositoryBean
public interface AppJPABaseDao<T,ID extends Serializable> extends AppJpaDataRepository<T, ID> {
}
