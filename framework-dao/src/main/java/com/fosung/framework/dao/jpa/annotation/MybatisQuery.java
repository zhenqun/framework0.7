package com.fosung.framework.dao.jpa.annotation;

import org.springframework.data.annotation.QueryAnnotation;

import java.lang.annotation.*;

/**
 * 对mybatis查询方法和类的注解
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD , ElementType.TYPE})
@QueryAnnotation
@Documented
public @interface MybatisQuery {
}
