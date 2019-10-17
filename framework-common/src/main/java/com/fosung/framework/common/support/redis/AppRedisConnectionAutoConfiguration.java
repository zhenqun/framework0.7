package com.fosung.framework.common.support.redis ;

import com.fosung.framework.common.support.redis.session.AppRedisSessionConfiguration;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.StringUtils;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 * redis自动化配置
 * @Author : liupeng
 * @Date : 2019-03-27
 * @Modified By
 */
@Slf4j
@Configuration
@Import(value = {AppRedisSessionConfiguration.class})
public class AppRedisConnectionAutoConfiguration extends AppRedisConnectionConfigurationAdaptor {

	private final RedisProperties properties;

	private final List<LettuceClientConfigurationBuilderCustomizer> builderCustomizers;

	public AppRedisConnectionAutoConfiguration(RedisProperties properties,
											   ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
											   ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider,
											   ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> builderCustomizers) {
		super(properties, sentinelConfigurationProvider, clusterConfigurationProvider);
		this.properties = properties;
		this.builderCustomizers = builderCustomizers
				.getIfAvailable(Collections::emptyList);
	}

	@Bean(destroyMethod = "shutdown")
	public DefaultClientResources lettuceClientResources() {
		return DefaultClientResources.create();
	}

	@Bean
	public LettuceConnectionFactory redisConnectionFactory(
			ClientResources clientResources) throws UnknownHostException {
		LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(
				clientResources, this.properties.getLettuce().getPool()) ;

		LettuceConnectionFactory lettuceConnectionFactory = createLettuceConnectionFactory(clientConfig) ;

		log.info("创建自定义的redisConnectionFactory, {}" , lettuceConnectionFactory.getClass().getName());

		return lettuceConnectionFactory ;
	}

	private LettuceConnectionFactory createLettuceConnectionFactory(
			LettuceClientConfiguration clientConfiguration) {
		if (getSentinelConfig() != null) {
			return new AppLettuceConnectionFactory(getSentinelConfig(), clientConfiguration);
		}
		if (getClusterConfiguration() != null) {
			return new AppLettuceConnectionFactory(getClusterConfiguration(),
					clientConfiguration);
		}
		return new AppLettuceConnectionFactory(getStandaloneConfig(), clientConfiguration);
	}

	private LettuceClientConfiguration getLettuceClientConfiguration(
			ClientResources clientResources, RedisProperties.Pool pool) {
		LettuceClientConfigurationBuilder builder = createBuilder(pool);
		applyProperties(builder);
		if (StringUtils.hasText(this.properties.getUrl())) {
			customizeConfigurationFromUrl(builder);
		}
		builder.clientResources(clientResources);
		customize(builder);
		return builder.build();
	}

	private LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
		if (pool == null) {
			return LettuceClientConfiguration.builder();
		}
		return new PoolBuilderFactory().createBuilder(pool);
	}

	private LettuceClientConfigurationBuilder applyProperties(
			LettuceClientConfigurationBuilder builder) {
		if (this.properties.isSsl()) {
			builder.useSsl();
		}
		if (this.properties.getTimeout() != null) {
			builder.commandTimeout(this.properties.getTimeout());
		}
		if (this.properties.getLettuce() != null) {
			RedisProperties.Lettuce lettuce = this.properties.getLettuce();
			if (lettuce.getShutdownTimeout() != null
					&& !lettuce.getShutdownTimeout().isZero()) {
				builder.shutdownTimeout(
						this.properties.getLettuce().getShutdownTimeout());
			}
		}
		return builder;
	}

	private void customizeConfigurationFromUrl(
			LettuceClientConfigurationBuilder builder) {
		ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
		if (connectionInfo.isUseSsl()) {
			builder.useSsl();
		}
	}

	private void customize(
			LettuceClientConfigurationBuilder builder) {
		for (LettuceClientConfigurationBuilderCustomizer customizer : this.builderCustomizers) {
			customizer.customize(builder);
		}
	}

	/**
	 * Inner class to allow optional commons-pool2 dependency.
	 */
	private static class PoolBuilderFactory {

		public LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool properties) {
			return LettucePoolingClientConfiguration.builder()
					.poolConfig(getPoolConfig(properties)) ;
		}

		private GenericObjectPoolConfig getPoolConfig(RedisProperties.Pool properties) {
			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(properties.getMaxActive());
			config.setMaxIdle(properties.getMaxIdle());
			config.setMinIdle(properties.getMinIdle());
			if (properties.getMaxWait() != null) {
				config.setMaxWaitMillis(properties.getMaxWait().toMillis());
			}
			return config;
		}

	}

}
