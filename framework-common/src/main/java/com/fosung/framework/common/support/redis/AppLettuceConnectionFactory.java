package com.fosung.framework.common.support.redis ;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * redis连接工厂
 * @Author : liupeng
 * @Date : 2019-03-27
 * @Modified By
 */
@Slf4j
public class AppLettuceConnectionFactory extends LettuceConnectionFactory {

    public AppLettuceConnectionFactory(RedisStandaloneConfiguration standaloneConfig,
                                    LettuceClientConfiguration clientConfig) {
        super( standaloneConfig , clientConfig ) ;
    }

    public AppLettuceConnectionFactory(RedisSentinelConfiguration sentinelConfiguration,
                                    LettuceClientConfiguration clientConfig) {
        super( sentinelConfiguration , clientConfig ) ;
    }

    public AppLettuceConnectionFactory(RedisClusterConfiguration clusterConfiguration,
                                    LettuceClientConfiguration clientConfig) {

        super( clusterConfiguration , clientConfig ) ;
    }

    @Override
    public RedisConnection getConnection() {
        RedisConnection redisConnection = super.getConnection() ;

        return createRedisConnectionProxy( redisConnection ) ;
    }

    /**
     * 创建redis连接
     * @param targetRedisConnection
     * @return
     */
    public RedisConnection createRedisConnectionProxy(RedisConnection targetRedisConnection){
        // 如果已经是aop代理，则不再重新搭建
        if( AopUtils.isAopProxy( targetRedisConnection ) ){
            log.info("RedisConnection已经是代理对象");
            return targetRedisConnection ;
        }

        ProxyFactory proxyFactory = new ProxyFactory() ;
        proxyFactory.setTarget( targetRedisConnection ) ;
        proxyFactory.setInterfaces( RedisConnection.class ) ;
        proxyFactory.setProxyTargetClass( true ) ;
        // 设置目标拦截器
        proxyFactory.addAdvice( new AppRedisConnectionMethodInterceptor() ) ;

        return (RedisConnection)proxyFactory.getProxy() ;
    }
}
