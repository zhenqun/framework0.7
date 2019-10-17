package com.fosung.framework.task.config;

import com.fosung.framework.task.AppScheduledAnnotationBeanPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

/**
 * @author toquery
 * @version 1
 */
@Slf4j
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class AppSchedulingConfiguration {

    public AppSchedulingConfiguration() {
        log.info("加载 App Task 配置类 {} ", this.getClass().getSimpleName());
    }

    @Bean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AppScheduledAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
        return new AppScheduledAnnotationBeanPostProcessor();
    }

}
