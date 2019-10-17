package com.fosung.framework.dao.config.mybatis.support;

import java.lang.annotation.*;

/**
 * mybatis中Dao层接口的标识，只有带有这个标识的接口才会被mybatis自动代理和实现
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MybatisRepository {

}
