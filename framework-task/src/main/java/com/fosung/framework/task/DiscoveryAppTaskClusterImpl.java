package com.fosung.framework.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author toquery
 * @version 1
 */
@Slf4j
public class DiscoveryAppTaskClusterImpl implements AppTaskCluster {

    @Resource
    protected DiscoveryClient discoveryClient;

    @Resource
    private EurekaInstanceConfigBean eurekaInstanceConfigBean;

    @Override
    public boolean needRunTask() {
        // 获取当前应用的所有服务
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(eurekaInstanceConfigBean.getAppname());
        // 获取当前应用所服务服务的主机ip并排序
        List<String> serviceInstanceHosts = serviceInstances.stream().map(ServiceInstance::getHost).sorted().collect(Collectors.toList());
        // 定位当前主机服务的顺序号
        int localServiceInstanceIndex = serviceInstanceHosts.indexOf(eurekaInstanceConfigBean.getIpAddress());
        // 如果是集群第一个则执行，否则不执行
        return localServiceInstanceIndex == 0;
    }
}
