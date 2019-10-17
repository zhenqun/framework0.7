package com.fosung.framework.common.secure.dataccess.strategy;

import com.fosung.framework.common.secure.dataccess.APIDataAccessPrincipal;
import com.fosung.framework.common.secure.dataccess.exception.APIDataAccessException;

import java.util.Map;

/**
 * @Author : liupeng
 * @Date : 2018/8/22 10:59
 * @Modified By
 */
public interface APIDataAccessAuthStrategy {

   /**
    * 保存实体相关的数据访问权限
    * @param record
    * @param dataAccessPrincipal
    */
   default void saveOrUpdateDataAccessAuth(Object record, APIDataAccessPrincipal dataAccessPrincipal) {

   }

   /**
    * 格式化或填充查询条件
    * @param searchParams
    * @param dataAccessPrincipal
    * @return
    */
   default Map<String,Object> fillAccessAuthQueryParam(Map<String, Object> searchParams, APIDataAccessPrincipal dataAccessPrincipal) {
      return searchParams;
   }

   /**
    * 是否有数据访问权限
    * @param entity
    * @param dataAccessPrincipal
    * @return
    * @throws APIDataAccessException
    */
   default boolean isAccessAllowed(Object entity, APIDataAccessPrincipal dataAccessPrincipal) throws APIDataAccessException {
      return true ;
   }



}
