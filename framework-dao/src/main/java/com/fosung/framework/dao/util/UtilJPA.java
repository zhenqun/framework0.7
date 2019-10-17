package com.fosung.framework.dao.util;

import ch.qos.logback.core.db.dialect.H2Dialect;
import com.fosung.framework.common.util.UtilReflection;
import com.fosung.framework.dao.dialect.MySQLDialectWithoutFK;
import com.fosung.framework.dao.dialect.PostgreSQL9DialectWithoutFK;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.SQLServer2008Dialect;
import org.springframework.orm.jpa.vendor.Database;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * JPA帮助类
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@SuppressWarnings("all")
public class UtilJPA {
	/**
	 * 简单映射类型
	 */
	public static Class[] simpleMapperClasses = {
		String.class , Date.class
	} ;
	/**
	 * 获取实体中所有简单的数据库映射字段
	 */
	public static List<String> getSimpleORMFieldInEntity(Class entityClass){
		if(entityClass==null){
			return null ;
		}
		//获取所有声明的字段
		Set<Field> fields = UtilReflection.getAllFields(entityClass) ;
		List<String> simpleFields = new ArrayList<String>() ;
		for (Field field : fields) {
			if(isSimpleORMField(field)){
				simpleFields.add(field.getName()) ;
			}
		}
		return simpleFields ;
	}

	/**
	 * 判断字段是否为简单类型的映射
	 */
	public static boolean isSimpleORMField(Field field){
		boolean flag = Collection.class.isAssignableFrom(field.getType()) ? 
				false : Map.class.isAssignableFrom(field.getType()) ?
						false : true ;
		return flag ;
	}

	/**
	 * 获取数据库类型
	 */
	public static Database getDBType(DataSource dataSource){
		String jdbcUrl = getJdbcUrlFromDataSource(dataSource);
		
		if (StringUtils.contains(jdbcUrl, ":h2:")) {
			return Database.H2 ;
		} else if (StringUtils.contains(jdbcUrl, ":mysql:")) {
			return Database.MYSQL ;
		} else if (StringUtils.contains(jdbcUrl, ":oracle:")) {
			return Database.ORACLE ;
		} else if (StringUtils.contains(jdbcUrl, ":postgresql:")) {
			return Database.POSTGRESQL ;
		} else if (StringUtils.contains(jdbcUrl, ":sqlserver:")) {
			return Database.SQL_SERVER ;
		}
		throw new IllegalArgumentException("Unknown Database of " + jdbcUrl);
	}
	/**
	 * 从DataSoure中取出connection, 根据connection的metadata中的jdbcUrl判断Dialect类型.
	 * 仅支持Oracle 10, H2, MySql, PostgreSql, SQLServer，如需更多数据库类型，请仿照此类自行编写。
	 */
	public static String getDialect(DataSource dataSource) {
		switch (getDBType(dataSource)) {
			case H2:
				return H2Dialect.class.getName();
			case MYSQL:
				return MySQLDialectWithoutFK.class.getName();
//				return MySQL5InnoDBDialect.class.getName() ;
			case POSTGRESQL:
				return PostgreSQL9DialectWithoutFK.class.getName() ;
			case SQL_SERVER:
				return SQLServer2008Dialect.class.getName();
		}
		
		throw new IllegalArgumentException("未知的数据库类型");
	}
	/**
	 * 由dataSource中获取jdbc连接的url
	 * @param dataSource
	 * @return
	 */
	private static String getJdbcUrlFromDataSource(DataSource dataSource) {
		Connection connection = null;
		try {
//			//如果是主从库，则从master库获取连接
//			if( dataSource instanceof MasterSlaveDataSource){
//				connection = ((MasterSlaveDataSource)dataSource).getDataSource(SQLStatementType.INSERT).
//						getConnection() ;
//			}else{
//
//			}

			connection = dataSource.getConnection();

			if (connection == null) {
				throw new IllegalStateException("Connection returned by DataSource [" + dataSource + "] was null");
			}
			return connection.getMetaData().getURL();
		} catch (SQLException e) {
			throw new RuntimeException("Could not get database url", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}
}
