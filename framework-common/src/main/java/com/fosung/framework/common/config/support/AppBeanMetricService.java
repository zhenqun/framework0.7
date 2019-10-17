package com.fosung.framework.common.config.support;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.List;

/**
 * 记录系统中运行bean的信息
 * @Author : liupeng
 * @Date : 2018-12-01
 * @Modified By
 */
@Slf4j
public class AppBeanMetricService implements BeanPostProcessor {

    @Getter
    private List<String> appBeanNames = Lists.newArrayList() ;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if( bean==null || appBeanNames.contains( bean.getClass().getName() )){
            return bean ;
        }

        appBeanNames.add( bean.getClass().getName() ) ;

        return bean;
    }
}
