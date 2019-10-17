package com.fosung.framework.dao.jpa.support;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.util.UtilEnum;
import com.fosung.framework.common.util.UtilEscape;
import com.fosung.framework.common.util.UtilReflection;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.dao.support.AppSearchFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;

@SuppressWarnings("all")
@Slf4j
public class DynamicJPASpecifications {
	
	/**
	 * 构建jpa查询所需要的Specification对象
	 * @param searchParams 查询参数，格式要能够被SearchFilter解析
	 * @param entityClazz 查询的目标对象
	 * @return jpa查询需要的Specification对象
	 */
	public static <T> Specification<T> bySearchParam(LinkedHashMap<String, Object> searchParams, final Class<T> entityClazz) {
		LinkedHashMap<String, AppSearchFilter> filters = AppSearchFilter.parse(searchParams) ;
		
		return bySearchFilter(filters , entityClazz) ;
	}
	/**
	 * 构建支持缓存的查询对象
	 * @param filters
	 * @param entityClazz
	 * @param cache 是否缓存查询结果
	 * @return
	 */
	public static <T> Specification<T> bySearchFilter(final LinkedHashMap<String, AppSearchFilter> filters, final Class<T> entityClazz , final boolean cache) {
		return new Specification<T>() {
			/**
			 * 存储临时分组的map
			 */
			private Map<String, List<AppSearchFilter>> groupFilterMap = new LinkedHashMap<String, List<AppSearchFilter>>() ;
			/**
			 * 关联查询的map，暂时存储关联查询语句
			 */
			private Map<String, List<AppSearchFilter>> joinMap = new LinkedHashMap<String, List<AppSearchFilter>>() ;
			
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				if(MapUtils.isEmpty(filters)){
					return null ;
				}
				//按照查询条件进行分组
				for (Map.Entry<String, AppSearchFilter> filterEntry : filters.entrySet()) {
					if(groupFilterMap.containsKey(filterEntry.getValue().group)){
						groupFilterMap.get(filterEntry.getValue().group).add(filterEntry.getValue()) ;
					}else{
						List<AppSearchFilter> searchFilters  = new ArrayList<AppSearchFilter>() ;
						searchFilters.add(filterEntry.getValue()) ;
						groupFilterMap.put(filterEntry.getValue().group, searchFilters) ;
					}
				}
				
				Predicate allPredicate = null ;
				
				for (Map.Entry<String, List<AppSearchFilter>> filterGroupEntry : groupFilterMap.entrySet()) {
					Predicate predicate = null ;
					AppSearchFilter.Connector connector = filterGroupEntry.getValue().get(0).connector ;
					for (AppSearchFilter filter : filterGroupEntry.getValue()) {
						String[] attributeNames = filter.fieldName.split("[\\.]") ;
						if(attributeNames.length>2){
							throw new IllegalArgumentException(filter.fieldName + " 不支持超过2级的属性查询 ");
						}
						//对于关联属性特殊处理，层级属性之间用“.”连接
						if(attributeNames.length == 2){
							filter.attributeName = attributeNames[1] ;
							
							if(!joinMap.containsKey(attributeNames[0])){
								joinMap.put(attributeNames[0], new ArrayList<AppSearchFilter>()) ;
							}
							
							joinMap.get(attributeNames[0]).add(filter) ;
							
							continue ;
						}
						predicate = joinPredicate(entityClazz , predicate , root.get(attributeNames[0]) , filter , builder) ;
					}
					//构建整个查询条件
					allPredicate = joinPredicate(entityClazz , allPredicate , predicate , connector ,builder) ;
				}
				//清理现有分组查询map
				groupFilterMap.clear() ; 
//				构建关联查询
				boolean isPlural = buildJoin(joinMap , root , builder , entityClazz) ;
//				构建完关联关系之后，移除已有的关系
				joinMap.clear() ; 
//				唯一性查询
				query.distinct(isPlural) ;
				
				return allPredicate ;
			}
		};
	}
	/**
	 * 构建查询条件
	 * @param filters 被SearchFilter解析过的查询条件
	 * @param entityClazz 查询的目标对象
	 * @return jpa查询需要的Specification对象
	 */
	public static <T> Specification<T> bySearchFilter(final LinkedHashMap<String, AppSearchFilter> filters, final Class<T> entityClazz) {
		return bySearchFilter(filters, entityClazz, false) ;
	}
	/**
	 * 连接两个查询条件
	 * @param existPredicate 已有的查询条件
	 * @param expression 新查询条件表达式
	 * @param filter 查询源信息
	 * @param builder 查询条件构造器
	 * @return
	 */
	private static <T> Predicate joinPredicate(final Class<T> entityClazz , Predicate existPredicate , Expression expression , AppSearchFilter filter , CriteriaBuilder builder){
		//创建新的predicate
		Predicate newPredicate =  createPredicate(entityClazz , expression, filter, builder) ;
		//根据条件连接符构造查询条件
		return joinPredicate(entityClazz , existPredicate , newPredicate , filter.connector ,builder) ;
	}
	/**
	 * 构建查询条件
	 * @param existPredicate 已有查询
	 * @param newPredicate 新构建的查询
	 * @param connector 已有查询和新构建查询之间的连接符
	 * @param builder 查询构建器CriteriaBuilder
	 * @return 连接后的查询条件
	 */
	private static <T> Predicate joinPredicate(final Class<T> entityClazz , Predicate existPredicate , Predicate newPredicate , AppSearchFilter.Connector connector, CriteriaBuilder builder){
		if(newPredicate==null){
			return existPredicate ;
		}
		
		if(existPredicate==null){
			existPredicate = newPredicate ;
		}else{
			//存在两个predicate，使用条件连接符连接两个条件
			switch (connector) {
				case OR:
					existPredicate = builder.or(existPredicate , newPredicate) ;
					break ;
				default:
					existPredicate = builder.and(existPredicate , newPredicate) ;
					break ;
			}
		}
		return existPredicate ;
	}
	/**
	 * 构建关联关系。
	 * 如果存在集合关联关系，查询时需要使用distinct，以免出现多条记录，
	 * 使用distinct关键字后，order by的属性必须包含在select中。
	 * @param joinMap 查询条件
	 * @param root 查找的连接源
	 * @param builder 查询构造器
	 * @param entityClazz 查询实体
	 * @return 是否存在构建集合关联关系，如果存在，返回true，否则返回false。
	 */
	private static <T> Boolean buildJoin(Map<String, List<AppSearchFilter>> joinMap , Root<T> root , CriteriaBuilder builder , Class<T> entityClazz ){
		boolean flag = false ;
		for (Map.Entry<String, List<AppSearchFilter>> entry : joinMap.entrySet()) {
			Attribute attribute = root.getModel().getAttribute(entry.getKey()) ;
			//构建内连接对象
			Join join = null ;
			if(attribute instanceof SetAttribute){
				join = root.join((SetAttribute)attribute , JoinType.INNER) ;
			}else if(attribute instanceof ListAttribute){
				join = root.join((ListAttribute)attribute , JoinType.INNER) ;
			}else if(attribute instanceof CollectionAttribute){
				join = root.join((CollectionAttribute)attribute  , JoinType.INNER) ;
			}else if(attribute instanceof SingularAttribute){
				join = root.join((SingularAttribute) attribute , JoinType.INNER) ;
			}
			if(join==null){
				throw new IllegalArgumentException(entry.getKey() + " 无法获取get方法或不支持连接查询。");
			}
			//判断是否存在与集合的关联关系
			if(!flag && attribute instanceof PluralAttribute){
				flag = true ;
			}
			//构造关联查询条件
			Predicate predicate = null ;
			for (AppSearchFilter filter : entry.getValue()) {
				predicate = joinPredicate(entityClazz , predicate , join.get(filter.attributeName) , filter , builder) ;
			}
			//设置关联条件
			join.on(predicate) ;
		}
		return flag ;
	}
	
	/**
	 * 创建单个查询条件
	 * @param expression 待查询的属性表达式
	 * @param filter 
	 * @param builder
	 * @return
	 */
	private static <T> Predicate createPredicate(final Class<T> entityClazz , Expression expression , AppSearchFilter filter , CriteriaBuilder builder){
		
		Predicate predicate = null ;
		switch (filter.operator) {
			case EQ:
				predicate = builder.equal(expression, getFormattedValue( entityClazz , filter )) ;
				break;
			case NEQ:
				predicate = builder.notEqual(expression, getFormattedValue( entityClazz , filter )) ;
				break;
			//字符串类型比较
			case LIKE:
				predicate = builder.like(expression , "%" + getFormattedValue( entityClazz , filter ) + "%" , '/') ;
				break;
			case LLIKE:
				predicate = builder.like(expression , "%" + getFormattedValue( entityClazz , filter ), '/') ;
				break;
			case RLIKE:
				predicate = builder.like(expression , getFormattedValue( entityClazz , filter ) + "%", '/') ;
				break;
			case NLIKE:
				predicate = builder.notLike(expression , "%"+getFormattedValue( entityClazz , filter ) + "%", '/') ;
				break;
			//一般类型的比较处理
			case GT:
				predicate = builder.greaterThan(expression, (Comparable) getFormattedValue( entityClazz , filter )) ;
				break;
			case LT:
				predicate = builder.lessThan(expression, (Comparable) getFormattedValue( entityClazz , filter )) ;
				break;
			case GTE:
				predicate = builder.greaterThanOrEqualTo(expression, (Comparable) getFormattedValue( entityClazz , filter )) ;
				break;
			case LTE:
				predicate = builder.lessThanOrEqualTo(expression, (Comparable) getFormattedValue( entityClazz , filter )) ;
				break;
			//日期类型比较
			case EQDATE:
				predicate = builder.equal(expression, (Comparable) getFormattedValue( entityClazz , filter )) ;
				break;
			case NEQDATE:
				predicate = builder.notEqual(expression, (Comparable) getFormattedValue( entityClazz , filter )) ;
				break;
			case GTDATE:
				predicate = builder.greaterThan(expression, (Comparable) getFormattedValue( entityClazz , filter )) ;
				break;
			case LTDATE:
				predicate = builder.lessThan(expression, (Comparable) getFormattedValue( entityClazz , filter )) ;
				break;
			case GTEDATE:
				predicate = builder.greaterThanOrEqualTo(expression, (Comparable) getFormattedValue( entityClazz , filter )) ;
				break;
			case LTEDATE:
				predicate = builder.lessThanOrEqualTo(expression, (Comparable) getFormattedValue( entityClazz , filter )) ;
				break;
			//空值判断
			case ISNULL:
				predicate = builder.isNull(expression) ;
				break ;
			case ISNOTNULL:
				predicate = builder.isNotNull(expression) ;
				break ;
			case IN :
				Object[] values = getFormattedValueArray( entityClazz , filter ) ;

				//可以识别数组，可以迭代的几何或一般类型
//				if(filter.value.getClass().isArray()){
//					values = (Object[])filter.value ;
//				}else if(Iterable.class.isAssignableFrom(filter.value.getClass())){
//					values = StringUtils.join((Iterable)filter.value , ",").split(",") ;
//				}else if(ClassUtils.isPrimitiveOrWrapper(filter.value.getClass())){
//					values = new Object[]{filter.value} ;
//				}else{
//					values = String.valueOf(filter.value).split(",") ;
//				}
//
//				if( values!=null ){
//					// 构建格式化后的值
//					Object[] formattedValue = new Object[values.length] ;
//					for(int i = 0 ; i<values.length;i++){
//						Object tmpVal = getFormattedValue( entityClazz , filter , values[i] ) ;
//						formattedValue[ i ] = tmpVal ;
//					}
//					values = formattedValue ;
//				}

				if( values!=null && values.length>0 ){
					predicate = expression.in(values) ;
				}

				break ;

			case NOTIN :
				Object[] notInValues = getFormattedValueArray( entityClazz , filter ) ;
				//可以识别数组，可以迭代的几何或一般类型
//				if(filter.value.getClass().isArray()){
//					notInValues = (Object[])filter.value ;
//				}else if(Iterable.class.isAssignableFrom(filter.value.getClass())){
//					notInValues = StringUtils.join((Iterable)filter.value , ",").split(",") ;
//				}else if(ClassUtils.isPrimitiveOrWrapper(filter.value.getClass())){
//					values = new Object[]{filter.value} ;
//				}else{
//					notInValues = String.valueOf(filter.value).split(",") ;
//				}
//
//				if( notInValues!=null ){
//					// 构建格式化后的值
//					Object[] formattedValue = new Object[notInValues.length] ;
//					for(int i = 0 ; i<notInValues.length;i++){
//						Object tmpVal = getFormattedValue( entityClazz , filter , notInValues[i] ) ;
//						formattedValue[ i ] = tmpVal ;
//					}
//					notInValues = formattedValue ;
//				}

				if( notInValues!=null && notInValues.length>0 ){
					predicate = expression.in(notInValues).not() ;
				}

				break ;

			case BOOLEANQE :
				boolean flag = false ;

				if(filter.value!=null){
					if(filter.value instanceof Boolean){
						flag = (Boolean)filter.value ;
					}else if(filter.value instanceof Number){
						flag = ((Number)filter.value).intValue() > 0 ;
					}else if(filter.value instanceof String){
						flag = StringUtils.equalsIgnoreCase("true" , filter.value.toString()) ;
					}
				}

				predicate = flag ? builder.isTrue(expression) : builder.isFalse(expression) ;

				break ;
		}
		
		return predicate ;
	}

	/**
	 * 获取格式化的数组数据
	 * @param entityClazz
	 * @param filter
	 * @param <T>
	 * @return
	 */
	public static <T> Object[] getFormattedValueArray(final Class<T> entityClazz , AppSearchFilter filter ){
		if( filter == null || filter.value == null ){
			return null ;
		}
		//可以识别数组，可以迭代的几何或一般类型
		Object[] values = null ;
		if(filter.value.getClass().isArray()){
			values = (Object[])filter.value ;
		}else if(Iterable.class.isAssignableFrom(filter.value.getClass())){
			values = StringUtils.join((Iterable)filter.value , ",").split(",") ;
		}else if(ClassUtils.isPrimitiveOrWrapper(filter.value.getClass())){
			values = new Object[]{filter.value} ;
		}else{
			values = String.valueOf(filter.value).split(",") ;
		}

		if( values!=null ){
			// 构建格式化后的值
			Object[] formattedValue = new Object[values.length] ;
			for(int i = 0 ; i<values.length;i++){
				Object tmpVal = getFormattedValue( entityClazz , filter , values[i] ) ;
				formattedValue[ i ] = tmpVal ;
			}
			values = formattedValue ;
		}

		return values ;
	}

	/**
	 * 获取格式化之后的查询值
	 * @param filter
	 * @return
	 */
	public static <T> Object getFormattedValue(final Class<T> entityClazz , AppSearchFilter filter , Object filterValue){

		//获取所有的字段，包括父类中的字段
		Set<Field> fields = UtilReflection.getAllFields( entityClazz , UtilReflection.withName(filter.fieldName) ) ;

		Field field = fields!=null && fields.size()>0 ? fields.toArray(new Field[]{})[0] : null ;

		//获取字段类型
		Class<?> fieldClass = field!=null ? field.getType() : null ;

		Object value = getFormattedValue( fieldClass , filter.operator , filterValue) ;

		log.debug("查询字段->"+filter.fieldName+" , 查询值-> "+value);

		return value ;
	}

	/**
	 * 获取格式化之后的查询值
	 * @param filter
	 * @return
	 */
	public static <T> Object getFormattedValue(final Class<T> entityClazz , AppSearchFilter filter){
//		//获取所有的字段，包括父类中的字段
//		Set<Field> fields = UtilReflection.getAllFields( entityClazz , UtilReflection.withName(filter.fieldName) ) ;
//
//		Field field = fields!=null && fields.size()>0 ? fields.toArray(new Field[]{})[0] : null ;
//
//		//获取字段类型
//		Class<?> fieldClass = field!=null ? field.getType() : null ;
//
//		Object value = getFormattedValue( fieldClass , filter.operator , filter.value) ;
//
//		log.debug("查询字段->"+filter.fieldName+" , 查询值-> "+value);
//
//		return value ;

		return getFormattedValue( entityClazz , filter , filter.value ) ;
	}

	/**
	 * 获取格式化之后的查询值
	 * @param operator 操作符
	 * @param value 操作值
     * @return
     */
	public static Object getFormattedValue(Class<?> fieldClass , AppSearchFilter.Operator operator , Object value){
		//过滤空值
		if(value==null || UtilString.equalsIgnoreCase( "null" , value.toString() ) ){
			return value ;
		}

		if( fieldClass!=null && fieldClass.isEnum() && value instanceof String ){
			// 将枚举类的字符串转换成枚举对象
			try {
				value = UtilEnum.getEnum( (Class< ? extends Enum>)fieldClass , value.toString().trim() ) ;
			}catch (Exception e){
			    e.printStackTrace() ;
			}

		}else if(operator.applyClass.getName().equals(Date.class.getName())&&
				!(value instanceof Date)){
			//将字符串转换为日期
			String dateString = value.toString().trim() ;
			String[] datePatterns = {AppProperties.DATE_PATTERN , AppProperties.DATE_TIME_PATTERN , "HH:mm:ss"} ;
			try {
				value = DateUtils.parseDate(dateString , datePatterns) ;
			} catch (ParseException e) {
				log.error("解析日期格式错误。"+e.getMessage());
			}

		}else if(operator.applyClass.getName().equals(String.class.getName())){
			//为特殊字符加上转义字符
			value = UtilEscape.escapeSQL(value.toString()) ;
		}

		return value ;
	}
	
}
