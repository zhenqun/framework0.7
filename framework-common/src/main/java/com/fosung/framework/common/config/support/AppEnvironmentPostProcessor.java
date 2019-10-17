package com.fosung.framework.common.config.support;

import com.fosung.framework.common.json.JsonMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 加载自动配置的文件
 * @Author : liupeng
 * @Date : 2018-12-01
 * @Modified By
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppEnvironmentPostProcessor implements EnvironmentPostProcessor {

    // 默认配置信息
    public static final String APP_DEFAULT_CONFIG_RESOURCE_LOCATION = "app-config-default.properties";

    // Environment中自定义配置属性集的名称
    public static final String APP_CONFIG_NAME  = "appDefaultProperties" ;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Properties appDefaultProperties = loadAppDefaultConfigProperties() ;

        System.out.println("app默认配置: "+JsonMapper.toJSONString( appDefaultProperties , true ));

        PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource(
                APP_CONFIG_NAME , appDefaultProperties ) ;

        environment.getPropertySources().addLast( propertiesPropertySource ) ;
    }

    /**
     * 装载应用默认的配置
     * @return
     */
    protected Properties loadAppDefaultConfigProperties() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader() ;

        List<Properties> propertiesList = Lists.newArrayList() ;

        try {
            Enumeration<URL> urls = classLoader.getResources( APP_DEFAULT_CONFIG_RESOURCE_LOCATION );

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement() ;

                Properties properties = PropertiesLoaderUtils.loadProperties( new UrlResource(url)  ) ;

                propertiesList.add( properties ) ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Properties appDefaultProperties = new Properties() ;

        if( propertiesList != null ){
            for (Properties defaultProperty : propertiesList) {
                for (Map.Entry<Object, Object> entry : defaultProperty.entrySet()) {
                    appDefaultProperties.putIfAbsent( entry.getKey() , entry.getValue() ) ;
                }
            }
        }

        return appDefaultProperties ;

    }

}
