package com.fosung.framework.dao.support.service.jpa;

import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.secure.auth.AppUserDetailsService;
import com.fosung.framework.common.support.dao.entity.AppJpaBaseEntity;
import com.fosung.framework.common.support.dao.entity.AppJpaIdEntity;
import com.fosung.framework.common.support.dao.entity.AppJpaSoftDelEntity;
import com.fosung.framework.dao.jpa.AppJpaDataRepository;
import com.fosung.framework.dao.jpa.support.DynamicJPASpecifications;
import com.fosung.framework.dao.support.service.AppBaseDataServiceImpl;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体服务操作类，默认事务类型为只读，对执行过程中出现的所有异常全部回滚
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@Slf4j
@Transactional(readOnly = true , rollbackFor = Throwable.class )
public abstract class AppJPABaseDataServiceImpl<T, D extends AppJpaDataRepository<T, Long>> extends AppBaseDataServiceImpl<T, D, Long> {

    @Autowired
    protected D entityDao;

    @Autowired
    protected AppUserDetailsService appUserDetailsService;

    /**
     * 删除后的处理函数
     *
     * @param id
     */
    @Override
    public void postDeleteHandler(Long id) {
        super.postDeleteHandler( id ) ;
        //在进行刷新操作前，先刷新当前实体缓存
        this.entityDao.flush() ;
    }

    /**
     * 是否是软删除。如果是软删除需要在响应的底层服务中进行相关逻辑处理
     *
     * @return
     */
    public boolean isSoftDel() {
        boolean isSoftDel = ClassUtils.isAssignable(this.entityDao.getDomainClass(), AppJpaSoftDelEntity.class);
        if (isSoftDel) {
            log.info("{} 删除为软删除", this.entityDao.getDomainClass().getName());
        }
        return isSoftDel;
    }

    @Override
    public boolean isExist(Map<String, Object> searchParams) {
        LinkedHashMap<String, Object> queryExpressionMap = formatQueryExpression(searchParams);
        Specification<T> specification = DynamicJPASpecifications.bySearchParam(queryExpressionMap,
                entityDao.getDomainClass());
        return entityDao.count(specification) > 0;
    }

    @Transactional
    @Override
    public void delete(T entity) {
        if (entity == null) {
            return;
        }
        if (entity instanceof AppJpaIdEntity) {
            delete(((AppJpaIdEntity) entity).getId());
        } else {
            log.error("请根据实体id进行删除。");
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (isBlankId(id)) {
            return;
        }
        if (isSoftDel()) {
            T entity = get(id);
            if (entity != null) {
                //设置软删除
                ((AppJpaSoftDelEntity) entity).setDel(true) ;

                this.update(entity, Arrays.asList("del")) ;
            }
        } else {
            super.delete(id) ;
        }

    }

    @Override
    public long count(Map<String, Object> searchParams) {
        LinkedHashMap<String, Object> queryExpressionMap = formatQueryExpression(searchParams);
        Specification<T> specification = DynamicJPASpecifications.bySearchParam(queryExpressionMap,
                entityDao.getDomainClass());
        return entityDao.count(specification);
    }

    @Override
    public Page<T> queryByPage(Map<String, Object> searchParams, int pageNum, int pageSize, String[] sorts) {
        log.debug("获取的原始查询参数->" + JsonMapper.toJSONString(searchParams));

        Specification<T> specification = getQuerySpecification(searchParams);

        Pageable pageable = PageRequest.of(pageNum, pageSize, getSort(sorts));

        return entityDao.findAll(specification, pageable);
    }

    @Override
    public List<T> queryAll(Map<String, Object> searchParams, String[] sorts) {
        Specification<T> specification = getQuerySpecification(searchParams);

        return entityDao.findAll(specification, getSort(sorts));
    }

    /**
     * 获取查询条件的Specification对象
     *
     * @param searchParams 查询条件map
     * @return
     */
    public Specification<T> getQuerySpecification(Map<String, Object> searchParams) {
        return getQuerySpecification( searchParams , true ) ;
    }

    /**
     * 获取查询条件的Specification对象
     *
     * @param searchParams 查询条件map
     * @param enableDataAccessAuth 是否使用数据权限校验
     * @return
     */
    public Specification<T> getQuerySpecification(Map<String, Object> searchParams , boolean enableDataAccessAuth) {
        LinkedHashMap<String, Object> queryExpressionMap = formatQueryExpression(searchParams , enableDataAccessAuth);

        //如果是软删除，默认查询未删除的记录
        if (isSoftDel()) {
            if (queryExpressionMap == null) {
                queryExpressionMap = Maps.newLinkedHashMap();
            }
            if (!queryExpressionMap.containsKey("del:BOOLEANQE")) {
                queryExpressionMap.put("del:BOOLEANQE", false);
                log.info("添加软删除的查询参数 del:BOOLEANQE，查询没有删除的记录。");
            }
        }

        //构建查询条件
        Specification<T> specification = DynamicJPASpecifications.bySearchParam(queryExpressionMap,
                entityDao.getDomainClass());

        return specification;
    }

    @Override
    protected Sort getSort(String[] sorts) {
        //默认按照创建时间排序
        if ((sorts == null || sorts.length < 1) && AppJpaBaseEntity.class.isAssignableFrom(this.entityDao.getDomainClass())) {
            sorts = new String[]{"createDatetime"};
        }
        return super.getSort(sorts);
    }
}
