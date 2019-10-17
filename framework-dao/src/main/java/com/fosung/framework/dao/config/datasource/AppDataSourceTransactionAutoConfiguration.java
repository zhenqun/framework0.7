package com.fosung.framework.dao.config.datasource;

import com.fosung.framework.dao.config.AppDaoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 数据库事务配置类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = AppDaoProperties.PREFIX + ".datasource.enabled" , matchIfMissing = true , havingValue = "true")
@ConditionalOnMissingBean(PlatformTransactionManager.class)
@EnableTransactionManagement(proxyTargetClass=true)
public class AppDataSourceTransactionAutoConfiguration {

	public AppDataSourceTransactionAutoConfiguration(){
		log.info("初始化 事务管理器");
	}
	/**
	 * 应用事务管理
	 * @param dataSource
	 * @return
	 */
	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource){
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager() ;
		dataSourceTransactionManager.setDataSource(dataSource);

		log.info("创建事务管理器:{}" , dataSourceTransactionManager.getClass().getName());
		
		return dataSourceTransactionManager ;
	}
}