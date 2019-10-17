package com.fosung.framework.dao.jpa;

import com.fosung.framework.dao.jpa.lookup.QueryLookupStrategyFactories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import java.util.Optional;

/**
 * 自定义的JpaRepositoryFactory，用于生成JpaRepository
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@Slf4j
public class AppJpaRepositoryFactory extends JpaRepositoryFactory {

    private EntityManager entityManager;

    protected final QueryExtractor extractor;

    protected BeanFactory beanFactory;

    public AppJpaRepositoryFactory(EntityManager entityManager, BeanFactory beanFactory) {
        super(entityManager);
        //设置当前类的实体管理器
        this.entityManager = entityManager;

        this.extractor = PersistenceProvider.fromEntityManager(entityManager);

        this.beanFactory = beanFactory;
    }

    /**
     * 原方法
     * protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository( RepositoryInformation information, EntityManager entityManager)
     */
    @Override
    protected SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {

        log.info("创建实体{}的DAO实现类:{}", information.getDomainType().getName(), information.getRepositoryBaseClass().getName());

        return super.getTargetRepository(information, entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        //自定接口实现方法的基类
        return AppJpaDataRepositoryImpl.class;
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key, EvaluationContextProvider evaluationContextProvider) {
        QueryLookupStrategy queryLookupStrategy = QueryLookupStrategyFactories.create( entityManager ,
                beanFactory, key, extractor, evaluationContextProvider) ;

        return Optional.of(queryLookupStrategy);
    }

}
