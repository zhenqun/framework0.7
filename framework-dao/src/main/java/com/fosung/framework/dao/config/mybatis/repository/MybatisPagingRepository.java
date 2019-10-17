package com.fosung.framework.dao.config.mybatis.repository;

import com.fosung.framework.dao.config.mybatis.page.MybatisPage;
import com.fosung.framework.dao.config.mybatis.page.MybatisPageable;
import com.fosung.framework.dao.config.mybatis.support.MybatisRepository;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * mybatis分页查询接口
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@MybatisRepository
public interface MybatisPagingRepository<T> {

    /**
     * 查询list结果
     * @param searchParam 查询参数map
     * @return list对象
     */
    public List<T> findAll(@Param("param") Map<String, Object> searchParam) ;
    /**
     * 分页查询
     * @param searchParam 查询参数map
     * @param pageable 分页对象，使用new MybatisPageRequest(pageNum, pageSize)创建
     * @return 分页之后的记录，实现接口org.springframework.data.domain.Page
     */
    public MybatisPage<T> findAll(@Param("param") Map<String, Object> searchParam, MybatisPageable pageable) ;

}
