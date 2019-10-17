package com.fosung.framework.dao.config.jpa;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.support.dao.handler.AppBaseDataHandler;
import com.fosung.framework.dao.config.AppDaoProperties;
import com.fosung.framework.dao.jpa.AppJpaRepositoryFactoryBean;
import com.fosung.framework.dao.support.service.handler.AppBaseDataHandlerDelegate;
import com.fosung.framework.dao.support.service.handler.AppBaseDataHandlerWithJPAEntity;
import com.fosung.framework.dao.support.service.handler.AppBaseDataHandlerWithUpdateFields;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * jpa自动化配置
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@Configuration
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(name = AppDaoProperties.PREFIX + ".jpa.enabled" , matchIfMissing = true , havingValue = "true")
@Import(value = { AppJPATransactionAutoConfiguration.class })
@EnableJpaRepositories(basePackages= { AppProperties.AppPackage.BASE_PACKAGE }  ,
	repositoryFactoryBeanClass = AppJpaRepositoryFactoryBean.class)
public class AppJPAAutoConfiguration {

	@Autowired
	private AppDaoProperties appDaoProperties ;

	public AppJPAAutoConfiguration(){
		log.info("初始化{}" , this.getClass().getSimpleName());
	}

	@Bean
	public HibernateJpaVendorAdapter jpaVendorAdapter(){
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter() ;
		return jpaVendorAdapter ;
	}

	/**
	 * 初始化jpa实体工厂
	 * @param dataSource 数据源
	 * @param hibernateJpaVendorAdapter
	 * @return
	 */
	@Bean(name="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource ,
			HibernateJpaVendorAdapter hibernateJpaVendorAdapter){
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean() ;
		
		entityManagerFactory.setDataSource(dataSource) ;
		//获取需要检测的包
		String[] packages = appDaoProperties.getJpa().getDaoPackage().toArray( new String[]{} ) ;

		log.info("dao扫描的包为:{}" , Arrays.toString( packages ) );

		entityManagerFactory.setPackagesToScan( packages );
		//jpa适配器使用hibernate
		entityManagerFactory.setJpaVendorAdapter(hibernateJpaVendorAdapter);
		
		//jpa属性
		Properties jpaProperties = new Properties() ;
		//不允许创建lob
		setHibernateProperty( AvailableSettings.NON_CONTEXTUAL_LOB_CREATION , jpaProperties , true ) ;

		//默认的schema
		setHibernateProperty(AvailableSettings.DEFAULT_SCHEMA , jpaProperties ,appDaoProperties.getJpa().getSchema()) ;
		//默认不显示sql，建议使用log的方式打印sql日志及参数：
		//<logger name="org.hibernate.SQL" level="DEBUG"></logger>
		//<logger name="org.hibernate.type" level="TRACE"></logger>
		setHibernateProperty(AvailableSettings.SHOW_SQL , jpaProperties , appDaoProperties.getJpa().isShowSql() ) ;
		//默认不对sql进行格式化
		setHibernateProperty(AvailableSettings.FORMAT_SQL , jpaProperties ,appDaoProperties.getJpa().isFormatSql() ) ;
		//sql更新策略，默认执行update更新ddl
		setHibernateProperty(AvailableSettings.HBM2DDL_AUTO , jpaProperties ,appDaoProperties.getJpa().getDdlAuto() ) ;
		//不生成hibernate的统计分析数据
		setHibernateProperty(AvailableSettings.GENERATE_STATISTICS , jpaProperties,false) ;
		//dao持久化的dialect方言
		setHibernateProperty(AvailableSettings.DIALECT , jpaProperties , appDaoProperties.getJpa().getDialect( appDaoProperties ) ) ;
		//不执行自动的数据提交
		setHibernateProperty(AvailableSettings.AUTOCOMMIT , jpaProperties , false) ;
		//ddl生成策略
		setHibernateProperty(AvailableSettings.HBM2DDL_FILTER_PROVIDER , jpaProperties , appDaoProperties.getJpa().getHbm2ddlFilterProvider());

		//强制不启用hibernate二级缓存，在需要使用缓存的地方，根据具体的业务规则使用不同的缓存方式，
		//todo 考虑集成spring-cache
		jpaProperties.put( AvailableSettings.USE_SECOND_LEVEL_CACHE , false) ;

		entityManagerFactory.setJpaProperties(jpaProperties) ;

		return entityManagerFactory ;
	}

	/**
	 * 设置hibernate的属性<br>
	 * todo 可以扩展自动扩展hibernate的所有属性
	 * @param propertyName
	 * @param jpaProperties
	 * @param value
	 */
	private void setHibernateProperty(String propertyName , Properties jpaProperties , Object value){
		if( value!=null ){
			jpaProperties.put(propertyName, value) ;
		}
	}

	/**
	 * 创建代理类处理
	 * @return
	 */
	@Bean
	public AppBaseDataHandlerDelegate appBaseDataHandlerDelegate(
			ObjectProvider<List<AppBaseDataHandler>> appBaseDataHandlerProvider ){
		AppBaseDataHandlerDelegate appBaseDataHandlerDelegate = new AppBaseDataHandlerDelegate() ;

		List<AppBaseDataHandler> appBaseDataHandlers = appBaseDataHandlerProvider.getIfAvailable() ;

		if( appBaseDataHandlers!=null ){
			for (AppBaseDataHandler appBaseDataHandler : appBaseDataHandlers) {
				appBaseDataHandlerDelegate.addAppBaseDataHandler( appBaseDataHandler );
			}
		}

		return appBaseDataHandlerDelegate ;
	}

	/**
	 * 对更新字段进行处理
	 * @return
	 */
	@Bean
	public AppBaseDataHandlerWithUpdateFields appBaseDataHandlerWithUpdateFields(){
		return new AppBaseDataHandlerWithUpdateFields() ;
	}

	/**
	 * 对 AppJpaIdEntity 进行处理
	 * @return
	 */
	@Bean
	public AppBaseDataHandlerWithJPAEntity appBaseDataHandlerWithJPAEntity(){
		return new AppBaseDataHandlerWithJPAEntity() ;
	}

}
