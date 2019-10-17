package com.fosung.framework.web.mvc.config.web.configurer;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.web.http.ResponseParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置 ResponseParam 的参数信息
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppResponseParamConfigurer implements WebMvcConfigurer {

    public AppResponseParamConfigurer(AppProperties appProperties){
        log.info("创建自定义的mvc配置 {} , 初始化{}的参数appProperties" ,
                this.getClass().getSimpleName() , ResponseParam.class.getSimpleName() ) ;

        Assert.notNull( appProperties , "appProperties不能为空" );

        ResponseParam.appProperties( appProperties ) ;
    }

}
