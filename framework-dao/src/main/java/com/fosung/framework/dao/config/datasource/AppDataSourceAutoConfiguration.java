package com.fosung.framework.dao.config.datasource;

import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.dao.config.AppDaoProperties;
import com.fosung.framework.dao.config.builder.DruidDataSourceBuilder;
import com.fosung.framework.dao.dynamic.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库连接池通用配置信息，存储数据库连接池通用的配置参数等。
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = AppDaoProperties.PREFIX + ".datasource.enabled", matchIfMissing = true, havingValue = "true")
public class AppDataSourceAutoConfiguration {

    @Autowired
    private AppDaoProperties appDaoProperties;

    public AppDataSourceAutoConfiguration() {
        log.info("初始化{}", this.getClass().getSimpleName());
    }


    /**
     * 获取数据库连接池，支持单源和多源数据库连接
     */
//    @Bean(destroyMethod = "close")
    @Bean
    public DataSource dataSource() {
        AppDaoProperties.DataSource dataSourceProperties = appDaoProperties.getDatasource();
        log.info("初始化数据库连接,配置信息为: {}", JsonMapper.toJSONString(dataSourceProperties, true));
//        return DruidDataSourceBuilder.getDataSource(dataSourceProperties);
        return appDaoProperties.getDynamic().isEnabled() ? getDynamicDataSource() : DruidDataSourceBuilder.getDataSource(dataSourceProperties);

    }

    /**
     * 获取默认
     *
     * @return
     */
    private DataSource getDynamicDataSource() {
        DynamicDataSource dataSource = new DynamicDataSource();
        String masterName = appDaoProperties.getDynamic().getMasterName();
        Map<String, AppDaoProperties.DataSource> dataSourceMap = appDaoProperties.getDynamic().getMultiple();
        AppDaoProperties.DataSource masterDataSourceProperties = dataSourceMap.get(masterName);
        dataSource.setDefaultTargetDataSource(DruidDataSourceBuilder.getDataSource(masterDataSourceProperties));
        Map<Object, Object> targetDataSources = new HashMap<>();
        dataSourceMap.forEach((key, value) -> targetDataSources.put(key, DruidDataSourceBuilder.getDataSource(value)));
        dataSource.setTargetDataSources(targetDataSources);
        return dataSource;
    }


}
