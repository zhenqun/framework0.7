package com.fosung.framework.common.secure.dataccess.strategy;

import com.fosung.framework.common.secure.dataccess.APIDataAccessPrincipal;
import com.fosung.framework.common.secure.dataccess.exception.APIDataAccessException;

import java.util.Map;

/**
 * 默认的数据访问权限控制。默认允许所有的访问、不保存权限信息、不填充权限查询条件
 * @Author : liupeng
 * @Date : 2018/8/23 15:22
 * @Modified By
 */
public class APIDataAccessAuthStrategyDefault implements APIDataAccessAuthStrategy {

    /**
     * 保存实体相关的数据访问权限
     * @param record
     * @param dataAccessPrincipal
     */
    @Override
    public void saveOrUpdateDataAccessAuth(Object record, APIDataAccessPrincipal dataAccessPrincipal) {

    }

    /**
     * 格式化或填充查询条件
     * @param searchParams
     * @param dataAccessPrincipal
     * @return
     */
    @Override
    public Map<String,Object> fillAccessAuthQueryParam(Map<String, Object> searchParams, APIDataAccessPrincipal dataAccessPrincipal) {
        return searchParams;
    }

    /**
     * 是否有数据访问权限
     * @param entity
     * @param dataAccessPrincipal
     * @return
     * @throws APIDataAccessException
     */
    @Override
    public boolean isAccessAllowed(Object entity, APIDataAccessPrincipal dataAccessPrincipal) throws APIDataAccessException {
        return true ;
    }

}
