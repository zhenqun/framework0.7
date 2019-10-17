package com.fosung.framework.task;

/**
 * 集群环境任务队列接口
 *
 * @author toquery
 * @version 1
 */
public interface AppTaskCluster{

    /**
     * 是否在当前节点定时任务
     * @return
     */
    boolean needRunTask();
}
