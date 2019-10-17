package com.fosung.framework.common.secure.dataccess;


import com.fosung.framework.common.secure.dataccess.exception.APIDataAccessException;

import java.util.Map;

/**
 * 数据访问入口service
 * @Author : liupeng
 * @Date : 2018/8/23 11:17
 * @Modified By
 */
public interface APIDataAccessAuthService {

    /**
     * 保存实体相关的数据访问权限
     * @param record
     */
    default void saveOrUpdateDataAccessAuth(Object record) {

    }

    /**
     * 格式化或填充查询条件
     * @param searchParams
     * @return
     */
    default Map<String,Object> fillAccessAuthQueryParam(Map<String, Object> searchParams) {
        return searchParams;
    }

    /**
     * 是否有数据访问权限
     * @param entity
     * @return
     * @throws APIDataAccessException
     */
    default boolean isAccessAllowed(Object entity) throws APIDataAccessException {
        return true ;
    }

}
