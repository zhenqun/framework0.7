package com.fosung.framework.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

/**
 * @author toquery
 * @version 1
 */
@Slf4j
public class AppScheduledAnnotationBeanPostProcessor extends ScheduledAnnotationBeanPostProcessor {

    public AppScheduledAnnotationBeanPostProcessor() {
        log.info("初始化task处理器：{}", this.getClass().getSimpleName());
    }
}
