package com.fosung.framework.common.secure.dataccess;

import com.fosung.framework.common.secure.dataccess.strategy.APIDataAccessAuthStrategy;
import com.fosung.framework.common.secure.dataccess.strategy.APIDataAccessAuthStrategyDefault;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 是否需要访问权限过滤的标识
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
@Documented
public @interface APIDataAccess {
    /**
     * 默认启用
     */
    @AliasFor("enable")
    boolean value() default true ;

    @AliasFor("value")
    boolean enable() default true ;

    /**
     * 拦截策略类
     * @return
     */
    Class<? extends APIDataAccessAuthStrategy> strategyClass() default APIDataAccessAuthStrategyDefault.class ;

}
