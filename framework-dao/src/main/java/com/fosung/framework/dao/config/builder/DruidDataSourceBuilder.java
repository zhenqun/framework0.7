package com.fosung.framework.dao.config.builder;

import com.alibaba.druid.pool.DruidDataSource;
import com.fosung.framework.dao.config.AppDaoProperties;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;

/**
 * druid数据源操作类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class DruidDataSourceBuilder {

    public static DataSource getDataSource(AppDaoProperties.DataSource dataSourceProperties) {
        DruidDataSource dataSource = new DruidDataSource();
        //设置数据库连接的名称
        dataSource.setName(StringUtils.isBlank(dataSourceProperties.getName()) ? DruidDataSource.class.getSimpleName() : dataSourceProperties.getName());

        dataSource.setDriverClassName(dataSourceProperties.getDriverClass());
        dataSource.setUrl(dataSourceProperties.getUrl());
        dataSource.setUsername(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());

        //分别设置最大和最小连接数
        dataSource.setMaxActive(dataSourceProperties.getPool().getMaxActive());
        dataSource.setMinIdle(dataSourceProperties.getPool().getMinIdle());
        //连接池里面的数据不自动提交
        dataSource.setDefaultAutoCommit(dataSourceProperties.getPool().isAutoCommit());
        //是否需要进行验证
        dataSource.setValidationQuery(dataSourceProperties.getPool().getValidationQuery());
        //由连接池中获取连接时，需要进行验证
        dataSource.setTestOnBorrow(true);
        //归还连接时不需要验证
        dataSource.setTestOnReturn(false);
        //连接空闲时进行验证
        dataSource.setTestWhileIdle(true);
        //连接有效的验证时间间隔
        dataSource.setValidationQueryTimeout(dataSourceProperties.getPool().getValidationQueryTimeout());
        //连接空闲验证的时间间隔
        dataSource.setTimeBetweenEvictionRunsMillis(dataSourceProperties.getPool().getTimeBetweenEvictionRunsMillis());

        //设置datasource监控，只对sql执行状态进行监控
//		try {
//			dataSource.setFilters("stat") ;
//		}catch (Exception e){
//			e.printStackTrace() ;
//		}

        return dataSource;

    }
}
