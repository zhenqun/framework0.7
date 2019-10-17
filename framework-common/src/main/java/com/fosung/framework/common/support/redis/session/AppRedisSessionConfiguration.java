package com.fosung.framework.common.support.redis.session ;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

/**
 * 基于redis的session配置
 * @Author : liupeng
 * @Date : 2019-03-27
 * @Modified By
 */
@Slf4j
public class AppRedisSessionConfiguration {

    @Bean
    public ConfigureRedisAction configureRedisAction(){
        log.error("禁止redis进行session客户端配置") ;
        return ConfigureRedisAction.NO_OP ;
    }

    @Bean
    public AppSessionTaskNoOper springSessionRedisTaskExecutor(){
        log.error("禁止redis进行消息通知任务") ;
        return new AppSessionTaskNoOper() ;
    }

}
