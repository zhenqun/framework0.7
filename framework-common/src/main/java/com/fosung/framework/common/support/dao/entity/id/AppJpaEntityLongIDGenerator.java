package com.fosung.framework.common.support.dao.entity.id;

import com.fosung.framework.common.id.snowflake.AppIDGenerator;
import com.fosung.framework.common.id.snowflake.AppIDPart;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * hibernate(jpa)生成记录时使用的id生成器
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
public class AppJpaEntityLongIDGenerator implements IdentifierGenerator, Configurable {

	/**
	 * 公用一个id生成器
	 */
	private static AppIDGenerator appIDGenerator = new AppIDGenerator() ;

	private static final Map<String,AppIDPart> ID_PART_MAP = Maps.newHashMap() ;

	/**
	 * 注册id的映射，可以根据不同的实体生成不同的id，便于以后分库和分表
	 * @param packagePrefix
	 * @param snowflakeIDPart
     */
	public static void registerIdPart(String packagePrefix , AppIDPart snowflakeIDPart){
		ID_PART_MAP.put( packagePrefix , snowflakeIDPart ) ;
	}

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {

	}

	/**
	 * 生成id
	 * @param session
	 * @param object
	 * @return
	 * @throws HibernateException
	 */
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		AppIDPart snowflakeIDPart = getSnowflakeIDPart( object ) ;

		return snowflakeIDPart==null ? appIDGenerator.getNextId( object ) :
				appIDGenerator.getNextId(snowflakeIDPart , object) ;
	}

	/**
	 * 根据不同的对象获取不同的id生成方式
	 * @param object
     */
	public AppIDPart getSnowflakeIDPart(Object object ){
		if( object==null ){
			return null ;
		}
		String targetPackageName = object.getClass().getPackage().getName() ;

		// 默认使用默认的id生成类
		AppIDPart appIDPart = appIDGenerator.getDefaultAppIDPart() ;

		// 初始化包长度
		int packageLength = 0 ;

		// 根据目标类的包名，判断长度最匹配的一个id生成类
		for (String packageName : ID_PART_MAP.keySet()) {
			if( !targetPackageName.startsWith( packageName ) ){
				continue;
			}
			//默认按照长度进行匹配
			if( packageName.length() >= packageLength ){
				appIDPart = ID_PART_MAP.get(packageName) ;
				packageLength = packageName.length() ;
			}
		}

		log.debug("{} 使用\"{}\"生成id" , object.getClass().getName() , appIDPart.getName()); ;

		return appIDPart ;
	}

}
