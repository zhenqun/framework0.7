package com.fosung.framework.common.config;

import com.fosung.framework.common.config.support.AppBeanMetricService;
import com.fosung.framework.common.util.UtilBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * AppProperties自动配置
 * @Author : liupeng
 * @Date : 2019-03-29
 * @Modified By
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({AppProperties.class, AppSecureProperties.class})
@Import(value = {AppCommonSecureConfiguration.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppAutoConfiguration {

    public AppAutoConfiguration() {
        log.info("初始化AppProperties");
    }

    @Bean
    public AppBeanMetricService appBeanMetricService(){
        return new AppBeanMetricService() ;
    }

    @Bean
    public UtilBeanFactory utilBeanFactory(){
        return new UtilBeanFactory() ;
    }

}
