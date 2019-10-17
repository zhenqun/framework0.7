package com.fosung.framework.task.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author toquery
 * @version 1
 */
@Setter
@Getter
@ConfigurationProperties(prefix = AppTaskProperties.PREFIX)
public class AppTaskProperties {
    /**
     * 配置信息的前缀
     */
    public static final String PREFIX = "app.task";

    private AppTaskTypeEnum type = AppTaskTypeEnum.DISCOVERY;

    // 集群 task 时，唯一 task 运行超时时间
    private long timeout = 10L;
}
