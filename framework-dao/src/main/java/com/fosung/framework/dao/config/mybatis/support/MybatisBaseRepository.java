package com.fosung.framework.dao.config.mybatis.support;

import java.io.Serializable;

/**
 * mybatis基本操作类，包括根据id添加、查询、更新和删除。
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@MybatisRepository
public interface MybatisBaseRepository<T, P extends Serializable>{

}
