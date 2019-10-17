package com.fosung.framework.common.support.redis.session ;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * session任务执行
 * @Author : liupeng
 * @Date : 2019-03-27
 * @Modified By
 */
@Slf4j
public class AppSessionTaskNoOper extends SimpleAsyncTaskExecutor {

    @Override
    protected void doExecute(Runnable task) {
        log.info("不执行执行redis session相关的监听任务:{}" ,  task.toString());
    }
}
