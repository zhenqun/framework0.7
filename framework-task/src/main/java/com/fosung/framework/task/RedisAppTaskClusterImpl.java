package com.fosung.framework.task;

import com.fosung.framework.common.exception.AppException;
import com.fosung.framework.task.config.AppTaskProperties;
import com.google.common.base.Strings;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author toquery
 * @version 1
 */
@Slf4j
public class RedisAppTaskClusterImpl implements AppTaskCluster, AppTaskClusterRedis {

    @Resource
    private AppTaskProperties appTaskProperties;
    @Resource
    protected StringRedisTemplate stringRedisTemplate;

    @Setter
    private String groupName;

    @Override
    public boolean needRunTask() {
        if (Strings.isNullOrEmpty(groupName)){
            throw new AppException("未设置 REDIS TASK 任务唯一标识！！！！");
        }
        String redisTaskKey = TASK_KEY + ":" + groupName;
        String redisData = stringRedisTemplate.opsForValue().get(redisTaskKey);
        if (Strings.isNullOrEmpty(redisData)) {
            stringRedisTemplate.opsForValue().set(redisTaskKey, "1", appTaskProperties.getTimeout(), TimeUnit.MINUTES);
            return true;
        }
        return false;
    }
}
