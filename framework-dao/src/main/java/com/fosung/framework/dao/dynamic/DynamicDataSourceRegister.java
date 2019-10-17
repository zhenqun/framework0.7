package com.fosung.framework.dao.dynamic;

import com.fosung.framework.dao.config.AppDaoProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author toquery
 * @version 1
 */
@Slf4j
public class DynamicDataSourceRegister {

    private AppDaoProperties appDaoProperties;

    public DynamicDataSourceRegister(AppDaoProperties appDaoProperties) {
        log.info(this.getClass().getSimpleName());
        this.appDaoProperties = appDaoProperties;
        registerMultipleDataSource();
    }

    public void registerMultipleDataSource() {
        Map<String, AppDaoProperties.DataSource> targetDataSources = appDaoProperties.getDynamic().getMultiple();
        DynamicDataSourceContextHolder.addAllDataSourceIds(targetDataSources.keySet());
    }


}

