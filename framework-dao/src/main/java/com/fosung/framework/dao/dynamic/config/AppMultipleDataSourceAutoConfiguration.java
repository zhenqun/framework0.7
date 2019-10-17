package com.fosung.framework.dao.dynamic.config;

import com.fosung.framework.dao.config.AppDaoProperties;
import com.fosung.framework.dao.dynamic.DynamicDataSourceRegister;
import com.fosung.framework.dao.dynamic.aop.DynamicDataSourceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author toquery
 * @version 1
 */
@Slf4j
@Configuration
@Import(DynamicDataSourceRegister.class)
@ConditionalOnProperty(prefix = AppDaoProperties.PREFIX + ".dynamic", name = "enabled", havingValue = "true")
public class AppMultipleDataSourceAutoConfiguration {

    public AppMultipleDataSourceAutoConfiguration() {
        log.info(getClass().getSimpleName());
    }

    @Bean
    public DynamicDataSourceAspect getDynamicDataSourceAspect() {
        return new DynamicDataSourceAspect();
    }
}
