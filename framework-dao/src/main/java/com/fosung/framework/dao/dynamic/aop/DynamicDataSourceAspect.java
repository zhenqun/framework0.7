package com.fosung.framework.dao.dynamic.aop;

import com.fosung.framework.dao.dynamic.DynamicDataSourceContextHolder;
import com.fosung.framework.dao.dynamic.annotation.TargetDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;

/**
 * @author toquery
 * @version 1
 */
@Slf4j
@Aspect
public class DynamicDataSourceAspect implements Ordered {

    public DynamicDataSourceAspect() {
        log.info("启用基于AOP的动态数据切换 : {}", getClass().getSimpleName());
    }

    @Before("@annotation(targetDataSource)")
    public void changeDataSource(JoinPoint point, TargetDataSource targetDataSource) throws Throwable {
        //获取当前的指定的数据源;
        String dataSource = targetDataSource.value();
        //如果不在我们注入的所有的数据源范围之内，那么输出警告信息，系统自动使用默认的数据源。
        if (!DynamicDataSourceContextHolder.containsDataSource(dataSource)) {
            log.error("数据源[{}]不存在，使用默认数据源 > {}", targetDataSource.value(), point.getSignature());
        } else {
            log.info("使用数据源 : {} > {}", targetDataSource.value(), point.getSignature());
            //找到的话，那么设置到动态数据源上下文中。
            DynamicDataSourceContextHolder.setDataSourceType(targetDataSource.value());
        }
    }

    @After("@annotation(targetDataSource)")
    public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        log.info("Revert DataSource : {} > {}", targetDataSource.value() + point.getSignature());
        //方法执行完毕之后，销毁当前数据源信息，进行垃圾回收。
        DynamicDataSourceContextHolder.clearDataSourceType();
    }

    //保证该AOP在@Transactional之前执行
    @Override
    public int getOrder() {
        return -10;
    }

}