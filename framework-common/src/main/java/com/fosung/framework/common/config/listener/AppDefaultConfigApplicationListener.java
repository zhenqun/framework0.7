package com.fosung.framework.common.config.listener;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * 系统基础关配置信息。
 */
@Slf4j
@Deprecated
public class AppDefaultConfigApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    @Override
    public int getOrder() {
        return AppProperties.ConfigurationOrder.getOrder( this.getClass().getSimpleName() , Ordered.LOWEST_PRECEDENCE ) ;
    }

    /**
     * 获取系统运行的默认配置
     * @param environment
     * @return
     */
    public Map<String,Object> getDefaultConfigProperties(Environment environment ){
        Map<String,Object> defaultConfig = Maps.newHashMap() ;

        //禁用JpaWebConfiguration中的openInView配置
        defaultConfig.put( "spring.jpa.open-in-view" , false ) ;

        //加入jodatime后，spring对时间转换处理的提醒
        defaultConfig.put( AppLoggingApplicationListener.LOGGING_CONFIG_PREFIX+"level.org.springframework.data.convert" , LogLevel.ERROR) ;

        log.info("系统默认配置信息:{}"  , JsonMapper.toJSONString( defaultConfig )) ;

        return defaultConfig ;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {

        Map<String,Object> defaultConfigProperties =  getDefaultConfigProperties( event.getEnvironment() ) ;

        //设置系统属性
        for (Map.Entry<String, Object> propertyItem : defaultConfigProperties.entrySet()) {
            if( System.getProperty( propertyItem.getKey() )!=null ){
                continue;
            }
            System.setProperty( propertyItem.getKey() , propertyItem.getValue().toString() ) ;
        }

        //设置环境变量属性
        event.getEnvironment().getPropertySources().addLast(
                new MapPropertySource( "default_config" , defaultConfigProperties )  ) ;
    }

}
