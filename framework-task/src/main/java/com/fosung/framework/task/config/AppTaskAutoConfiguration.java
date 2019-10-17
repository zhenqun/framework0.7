package com.fosung.framework.task.config;

import com.fosung.framework.task.DiscoveryAppTaskClusterImpl;
import com.fosung.framework.task.RedisAppTaskClusterImpl;
import com.fosung.framework.task.AppTaskCluster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 定时任务自动配置
 * @author : ToQuery
 */
@Slf4j
@EnableScheduling
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(AppTaskProperties.class)
@Import(AppSchedulingConfiguration.class)
public class AppTaskAutoConfiguration {

    public AppTaskAutoConfiguration() {
        log.info("正在初始化 App Task 配置 ，配置类 {} ", this.getClass().getSimpleName());
    }

    @Bean
    @ConditionalOnProperty(prefix = AppTaskProperties.PREFIX, name = "type", havingValue = "DISCOVERY", matchIfMissing = true)
    public AppTaskCluster getAppTaskCluster() {
        log.info("通过 DISCOVERY 方式保证 task 唯一性");
        return new DiscoveryAppTaskClusterImpl();
    }

    @Bean
    @ConditionalOnProperty(prefix = AppTaskProperties.PREFIX, name = "type", havingValue = "REDIS")
    public AppTaskCluster getAppTaskClusterRedis() {
        log.info("通过 REDIS 方式保证 task 唯一性");
        return new RedisAppTaskClusterImpl();
    }

}
