package com.fosung.framework.common.secure.signature.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 是否启用签名的标识
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
@Documented
public @interface APISignature {

    /**
     * 默认启用
     */
    @AliasFor("enable")
    boolean value() default true ;

    @AliasFor("value")
    boolean enable() default true ;

}