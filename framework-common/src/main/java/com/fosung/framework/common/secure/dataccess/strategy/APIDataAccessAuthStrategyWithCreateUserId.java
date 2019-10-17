package com.fosung.framework.common.secure.dataccess.strategy;

import com.fosung.framework.common.secure.dataccess.APIDataAccessPrincipal;
import com.fosung.framework.common.secure.dataccess.exception.APIDataAccessException;
import com.fosung.framework.common.support.dao.entity.AppJpaBaseEntity;
import com.fosung.framework.common.util.UtilString;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 基于当前用户判断是否具有操作权限
 * @Author : liupeng
 * @Date : 2018/8/23 15:22
 * @Modified By
 */
public class APIDataAccessAuthStrategyWithCreateUserId implements APIDataAccessAuthStrategy {

    /**
     * 管理员角色
     */
    @Getter
    private static String[] adminRoles = { "admin" } ;

    /**
     * 添加管理员角色
     * @param roles
     */
    public static void addAdminRoles(String... roles){
        List<String> tmpAdminRoles = Lists.newArrayList( adminRoles ) ;

        for (String role : roles) {
            if( UtilString.isBlank( role ) || tmpAdminRoles.contains( role ) ){
                continue;
            }
            tmpAdminRoles.add( role ) ;
        }

        adminRoles = tmpAdminRoles.toArray( new String[]{} ) ;
    }

    /**
     * 是否有数据访问权限
     * @return
     */
    @Override
    public boolean isAccessAllowed( Object entity , APIDataAccessPrincipal dataAccessPrincipal) throws APIDataAccessException {
        if (entity != null && entity instanceof AppJpaBaseEntity) {
            // 获取entity的创建人id
            AppJpaBaseEntity baseEntity = (AppJpaBaseEntity) entity;

            // 如果创建人id与当前线程id相等，或者前线程用户名角色是admin，则允许通过
            if ( dataAccessPrincipal.isSameUser( baseEntity.getCreateUserId() ) ||
                    dataAccessPrincipal.hasAnyRole( getAdminRoles() ) ){
                return true;
            }
        }
        throw new APIDataAccessException("没有操作权限");
    }

    /**
     * 格式化或填充查询条件，返回一个新的查询参数对象
     * @param searchParams
     * @param dataAccessPrincipal
     * @return 返回格式化后的数据
     */
    @Override
    public Map<String, Object> fillAccessAuthQueryParam(Map<String, Object> searchParams, APIDataAccessPrincipal dataAccessPrincipal) {
        String userId = dataAccessPrincipal.getUserId();

        Map<String,Object> formatedSearchParams = Maps.newLinkedHashMap() ;
        if( searchParams!=null ){
            formatedSearchParams.putAll( searchParams ) ;
        }

        // 当前用户id不为空 并且 不是管理员，则添加针对用户id查询的条件
        if ( ! UtilString.isEmpty(userId)  && ! dataAccessPrincipal.hasAnyRole( getAdminRoles() ) ) {
            if (! formatedSearchParams.containsKey("createUserId:EQ")) {
                formatedSearchParams.put("createUserId:EQ", userId);
            }
        }

        return formatedSearchParams ;
    }
}
