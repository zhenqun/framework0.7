package com.fosung.framework.common.support.anno;

import java.lang.annotation.*;

/**
 * 字典项父节点名称标识
 * @Author : liupeng
 * @Date : 2018-11-16
 * @Modified By
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE , ElementType.FIELD})
@Documented
public @interface AppDict {

    /**
     * 字典项父节点编码
     * @return
     */
    String value() default "";

}