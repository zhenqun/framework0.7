package com.fosung.framework.common.config.listener;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.google.common.collect.Maps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * 系统日志的相关配置信息。<br>
 * 需要注意：<br>
 * 1. 日志在系统启动时初始化，所以目前只能读取通过默认属性、命令行、System指定的属性配置<br>
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Deprecated
public class AppLoggingApplicationListener implements GenericApplicationListener {

    /**
     * 默认的日志目录和文件名称
     */
    public static final String DEFAULT_LOG_FILE = "log/logfile.log" ;

    /**
     * 默认的日志格式: “时间 - 日志级别 当前进程号 --- 线程名称 打印日志类名称 : 行号 - 日志内容 换行符”
     */
    private static final String DEFAULT_LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} -%5p ${PID:- } --- [%t] %-40.40logger{39} : %-5L - %m%n";

    /**
     * 日志配置的前缀
     */
    public static final String LOGGING_CONFIG_PREFIX = "logging." ;

    private static final Class<?>[] EVENT_TYPES = { ApplicationEnvironmentPreparedEvent.class };

    private static final Class<?>[] SOURCE_TYPES = { SpringApplication.class,
            ApplicationContext.class };

    @Override
    public int getOrder() {
        return AppProperties.ConfigurationOrder.getOrder( this.getClass().getSimpleName() , Ordered.HIGHEST_PRECEDENCE ) ;
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        if (type != null) {
            for (Class<?> supportedType : supportedTypes) {
                if (supportedType.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean supportsEventType(ResolvableType resolvableType) {
        return isAssignableFrom(resolvableType.getRawClass(), EVENT_TYPES);
    }

    @Override
    public boolean supportsSourceType(@Nullable Class<?> sourceType) {
        return isAssignableFrom(sourceType, SOURCE_TYPES);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        //此时Environment并没有完全初始化，只是加入了默认的属性
        if( event instanceof ApplicationEnvironmentPreparedEvent ){
            onApplicationEnvironmentPreparedEvent(
                    (ApplicationEnvironmentPreparedEvent) event ) ;
        }

    }

    /**
     * 在ApplicationEnvironmentPreparedEvent中处理log信息
     * @param event
     */
    private void onApplicationEnvironmentPreparedEvent( ApplicationEnvironmentPreparedEvent event ) {

        Map<String,Object> logProperties = getLogProperties( event.getEnvironment() ) ;

        //由于没有初始化日志，所以只能使用系统自带的输出
        System.out.println("日志配置: 日志文件初始目录,"+ logProperties.get( LogFile.FILE_PROPERTY )) ;

        System.out.println("log配置："+JsonMapper.toJSONString( logProperties , true ));

        event.getEnvironment().getPropertySources().addFirst( new MapPropertySource( "log_config" , logProperties )  ) ;
    }

    /**
     * 获取日志配置属性
     * @return
     */
    public Map<String,Object> getLogProperties( Environment environment ){
        Map<String,Object> logProperties = Maps.newHashMap() ;
        //日志输出文件
        setLogProperty( logProperties , environment , LogFile.FILE_PROPERTY  , DEFAULT_LOG_FILE ) ;
        //日志级别默认为info
        setLogProperty( logProperties , environment , LOGGING_CONFIG_PREFIX + "level.root"  , LogLevel.INFO ) ;
        //日志控制台输出日志的默认格式
        setLogProperty( logProperties , environment , LOGGING_CONFIG_PREFIX + "pattern.console", DEFAULT_LOG_PATTERN ) ;
        //日志文件存储日志的默认格式
        setLogProperty( logProperties , environment , LOGGING_CONFIG_PREFIX + "pattern.file", DEFAULT_LOG_PATTERN ) ;
        //日志文件最多存储30天
        setLogProperty( logProperties , environment , LOGGING_CONFIG_PREFIX + "file.max-history"  , 30 ) ;
        //单个日志文件的最大大小
        setLogProperty( logProperties , environment , LOGGING_CONFIG_PREFIX + "file.max-size"  , "10MB" ) ;

        return logProperties ;
    }

    /**
     * 设置日志属性，日志属性优先级：System、environment
     * @param logProperties
     * @param environment
     * @param key 日志key
     * @param defaultValue 默认值
     */
    public void setLogProperty( Map<String,Object> logProperties , Environment environment , String key , Object defaultValue ){
        Object value = defaultValue ;
        if( System.getProperty( key ) != null ){
            value = System.getProperty( key ) ;
        }else if( environment.containsProperty( key ) ){
            value = environment.getProperty( key ) ;
        }
        logProperties.put( key , value ) ;
    }

}
