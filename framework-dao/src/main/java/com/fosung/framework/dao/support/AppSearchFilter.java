package com.fosung.framework.dao.support;

import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * DAO查询支持的查询连接符和比较条件
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@SuppressWarnings("all")
public class AppSearchFilter {
	/**
	 * 字段和操作符之间的分隔符
	 */
	public static final String SPLITER = ":" ;
	/**
	 * sql查询字段名称
	 */
	public String fieldName;
	/*
	 * 查询值
	 */
	public Object value;
	
	public Operator operator;
	/**
	 * 属性名称
	 */
	public String attributeName ;
	/**
	 * sql语句所属组名称
	 */
	public String group ;
	
	public Connector connector ;
	
	public AppSearchFilter(String fieldName, Operator operator, Object value , Connector connector) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
		//属性名称默认和查询字段相同
		this.attributeName = fieldName ;
		this.connector = connector ;
	}

	/**
	 * searchParams中key的格式为FIELDNAME:OPERATOR 或 CONNECTOR:FIELDNAME:OPERATOR。
	 * 例如：id:EQ 或 AND:businessSystem.id:EQ将对当前实体属性businessSystem的id属性进行等值查询 ;
	 */
	public static LinkedHashMap<String, AppSearchFilter> parse(LinkedHashMap<String, Object> searchParams) {
		
		if(MapUtils.isEmpty(searchParams)){
			return null ;
		}
		
		//使用有序的列表
		LinkedHashMap<String, AppSearchFilter> filters = null ;
		
		for (Entry<String, Object> entry : searchParams.entrySet()) {
			// 过滤掉空值
			String key = entry.getKey() ;
			Object value = entry.getValue() ;
			
			// 拆分operator与filedAttribute
			String[] names = StringUtils.split(key, SPLITER);
			//构造不同条件之间的连接符
			Connector connector = null ;
			//查询字段
			String fieldName = null ;
			//根据名称获取操作标识
			Operator operator = null ;
			
			String group = "" ;
			
			if(names.length==2){
				//默认使用and连接符
				connector = Connector.AND ;
				fieldName = names[0] ;
				//最后一位为操作比较符号
				operator = Operator.valueOf(names[1]);
			}else if(names.length==3){
				//长度为3时，根据是否有连接符确定不同的格式
				if("AND".equals(names[0]) || "OR".equals(names[0]) ){
					connector = Connector.valueOf(names[0]) ;
					fieldName = names[1] ;
					//最后一位为操作比较符号
					operator = Operator.valueOf(names[2]);
				}else{
					connector = Connector.AND ;
					fieldName = names[0] ;
					operator = Operator.valueOf(names[1]);
					group = names[2] ;
				}
				
			}else if(names.length==4){
				connector = Connector.valueOf(names[0]) ;
				fieldName = names[1] ;
				//最后一位为操作比较符号
				operator = Operator.valueOf(names[2]);
				//组名称
				group = names[3] ;
			}else{
				throw new IllegalArgumentException(key + " is not a valid search filter name");
			}
			
			if (value==null || StringUtils.isBlank(value.toString())) {
				//操作标识的比较值不允许为null，则进行过滤
				if(!operator.isAllowNullValue){
					continue ;
				}else{
					value = null ;
				}
			}
			
			//创建searchFilter
			AppSearchFilter filter = new AppSearchFilter(fieldName, operator, value ,connector) ;
			//查询sql所属的组名称
			filter.group = group ;
			
			//初始化值
			if(filters==null){
				filters = Maps.newLinkedHashMap() ;
			}
			
			filters.put(key, filter);
		}
		
		return filters;
	}

	@Override
	public String toString() {
		return "SearchFilter [fieldName=" + fieldName + ", value=" + value
				+ ", operator=" + operator + "]";
	}

	public enum Operator {
		EQ(Object.class , false) , NEQ(Object.class , false) ,
		LIKE(String.class , false) , LLIKE(String.class , false) , RLIKE(String.class , false) , NLIKE(String.class , false) ,
		GT(Comparable.class , false) ,  LT(Comparable.class , false) ,  GTE(Comparable.class , false) , LTE(Comparable.class , false) ,
		EQDATE(Date.class , false) , NEQDATE(Date.class , false) , GTDATE(Date.class , false) ,  LTDATE(Date.class , false) ,  GTEDATE(Date.class , false) , LTEDATE(Date.class , false) ,
		ISNULL(Object.class , true) , ISNOTNULL(Object.class , true) ,
		IN(String.class , false) , NOTIN(String.class , false) , BOOLEANQE(Boolean.class , false) ;

		public Class applyClass ;

		public boolean isAllowNullValue = false ;

		Operator(Class applyClass , boolean isAllowNullValue) {
			this.applyClass = applyClass ;
			this.isAllowNullValue = isAllowNullValue ;
		}
	}

	public enum Connector{
		AND , OR
	}
	
}
