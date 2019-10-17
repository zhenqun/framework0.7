package com.fosung.framework.dao.support.service.handler;

import com.fosung.framework.common.secure.auth.AppUserDetails;
import com.fosung.framework.common.secure.auth.AppUserDetailsService;
import com.fosung.framework.common.support.dao.entity.AppJpaBaseEntity;
import com.fosung.framework.common.support.dao.entity.AppJpaIdEntity;
import com.fosung.framework.common.support.dao.handler.AppBaseDataHandlerAdaptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Date;

/**
 * 对jpa实体属性进行处理，只处理 AppJpaIdEntity 的子类
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@Slf4j
public class AppBaseDataHandlerWithJPAEntity extends AppBaseDataHandlerAdaptor {

    @Autowired
    protected AppUserDetailsService appUserDetailsService;

    @Override
    public boolean support(Class<?> entityClass) {
        return AppJpaIdEntity.class.isAssignableFrom( entityClass ) ;
    }

    @Override
    public <T> T preSaveHandler(T entity) {
        entity = super.preSaveHandler(entity) ;

        if (entity != null && entity instanceof AppJpaBaseEntity) {
            AppJpaBaseEntity baseEntity = (AppJpaBaseEntity) entity;
            baseEntity.setCreateDatetime(new Date()) ;
            baseEntity.setLastUpdateDatetime( new Date() );

            //设置记录创建人的id
            AppUserDetails appUserDetails = appUserDetailsService.getAppUserDetails();
            if (appUserDetails != null && appUserDetails.getUserId() != null) {
                baseEntity.setCreateUserId( appUserDetails.getUserId().toString() );
                baseEntity.setLastUpdateUserId( appUserDetails.getUserId().toString() );
            }
        }

        return entity ;
    }

    @Override
    public <T> T preUpdateHandler(T entity, Collection<String> updateFields) {
        Assert.notEmpty(updateFields, "必须指定更新的字段") ;

        entity = super.preUpdateHandler(entity, updateFields) ;

        //设置记录的更新时间
        if (entity != null && entity instanceof AppJpaBaseEntity) {
            AppJpaBaseEntity baseEntity = (AppJpaBaseEntity) entity;
            baseEntity.setLastUpdateDatetime(new Date());
            updateFields.add("lastUpdateDatetime");

            //设置记录更新人的id
            AppUserDetails appUserDetails = appUserDetailsService.getAppUserDetails();
            if ( appUserDetails != null && appUserDetails.getUserId() != null) {
                baseEntity.setLastUpdateUserId( appUserDetails.getUserId().toString() );

                updateFields.add("lastUpdateUserId");
            }
        }

        return entity ;
    }
}
