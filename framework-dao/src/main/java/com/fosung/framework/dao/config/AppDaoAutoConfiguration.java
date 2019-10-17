package com.fosung.framework.dao.config;

import com.fosung.framework.dao.config.datasource.AppDataSourceAutoConfiguration;
import com.fosung.framework.dao.config.jpa.AppJPAAutoConfiguration;
import com.fosung.framework.dao.config.mybatis.AppMybatisAutoConfiguration;
import com.fosung.framework.dao.dynamic.config.AppMultipleDataSourceAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * dao自动配置类，包括数据源、jpa、mybatis配置
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@Configuration
@EnableConfigurationProperties(AppDaoProperties.class)
@Import(value = {AppDataSourceAutoConfiguration.class, AppJPAAutoConfiguration.class, AppMybatisAutoConfiguration.class, AppMultipleDataSourceAutoConfiguration.class})
public class AppDaoAutoConfiguration {


}
