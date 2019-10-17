package com.fosung.framework.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形层级数据操作类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilTree {
	
	public static final String CHILDREN_KEY = "childrenKey" ;
	
	/**
	 * 由简单的数组对象构造树形结构的数据数组
	 * @param simpleTreeDataList 在结构上不存在层次关系的简单数组数据
	 * @param idField id属性名称
	 * @param parentIdField 关联父节点id字段的属性名称
	 * @param childrenKey 子节点名称，用于生成层次类型结构类型数据
	 * @param isRemoveIdField 在构造完成数据之后是否移除id和parentid字段
	 * @return
	 */
	public static List<Map<String, Object>> getTreeData(List<Map<String, Object>> simpleTreeDataList , String idField ,
			String parentIdField , String childrenKey , boolean isRemoveIdField){

		List<Map<String, Object>> resultMap = getTreeData(simpleTreeDataList , idField ,
				parentIdField , childrenKey , isRemoveIdField , false , false ) ;

		return resultMap ;
		
	}
	/**
	 * 由简单的数组对象构造树形结构的数据数组
	 * @param simpleTreeDataList 在结构上不存在层次关系的简单数组数据
	 * @param idField id属性名称
	 * @param parentIdField 关联父节点id字段的属性名称
	 * @param childrenKey 子节点名称，用于生成层次类型结构类型数据
	 * @param isRemoveIdField 在构造完成数据之后是否移除id字段
	 * @param isRemoveParentIdField 在构造完成数据之后是否移除parentid字段
	 * @return
	 */
	public static List<Map<String, Object>> getTreeData(List<Map<String, Object>> simpleTreeDataList , String idField ,
														String parentIdField , String childrenKey , boolean isRemoveIdField, boolean isRemoveParentIdField ){

		return getTreeData(simpleTreeDataList , idField , parentIdField , childrenKey , isRemoveIdField , isRemoveParentIdField , false ) ;
	}
	/**
	 * 由简单的数组对象构造树形结构的数据数组
	 * @param simpleTreeDataList 在结构上不存在层次关系的简单数组数据
	 * @param idField id属性名称
	 * @param parentIdField 关联父节点id字段的属性名称
	 * @param childrenKey 子节点名称，用于生成层次类型结构类型数据
	 * @param isRemoveIdField 在构造完成数据之后是否移除id字段
	 * @param isRemoveParentIdField 在构造完成数据之后是否移除parentid字段
	 * @param addLeafAttribute 添加叶子节点属性
	 * @return
	 */
	public static List<Map<String, Object>> getTreeData(List<Map<String, Object>> simpleTreeDataList , String idField ,
					String parentIdField , String childrenKey , boolean isRemoveIdField, boolean isRemoveParentIdField , boolean addLeafAttribute){
		//进行数据有效性校验
		if(StringUtils.isBlank(idField) || StringUtils.isBlank(parentIdField) ||
				simpleTreeDataList==null || simpleTreeDataList.size()<1){
			return null ;
		}
		//如果为空，设置为默认的子节点key
		if(StringUtils.isBlank(childrenKey)){
			childrenKey = CHILDREN_KEY ;
		}

		Map<String, Map<String, Object>> treeDataIDMap =
				new HashMap<String, Map<String, Object>>(simpleTreeDataList.size()) ;

		//构建treedata的id和实体的映射
		for (Map<String, Object> treeItemMap : simpleTreeDataList) {
			//是否为叶子节点
			if( addLeafAttribute ){
				treeItemMap.put("leaf" , true) ;
			}
			Object id = treeItemMap.get(idField) ;
			Assert.notNull(id , "id字段的值不能为空。");
			treeDataIDMap.put(id.toString() , treeItemMap ) ;
		}
		//格式化之后的结果数组
		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>(
				simpleTreeDataList.size());

		for (Map<String, Object> treeItemMap : simpleTreeDataList) {

			String parentId = null ;
			if(treeItemMap.get(parentIdField)!=null){
				parentId = treeItemMap.get(parentIdField).toString() ;
			}

			String id = treeItemMap.get(idField).toString() ;

			//在id映射map中存在父节点的映射
			if(treeDataIDMap.containsKey(parentId) && !StringUtils.equalsIgnoreCase(parentId, id)){
				Map<String, Object> parentTreeItemMap = treeDataIDMap.get(parentId) ;
				List<Map<String, Object>> childrenList = null ;
				if(parentTreeItemMap.containsKey(childrenKey)){
					childrenList = (List<Map<String, Object>>) parentTreeItemMap.get(childrenKey) ;
				}else{
					childrenList = new ArrayList<Map<String, Object>>() ;
					parentTreeItemMap.put(childrenKey, childrenList) ;
				}
				//父节点拥有子节点，则标识为非叶子节点
				if( addLeafAttribute ){
					parentTreeItemMap.put("leaf" , false) ;
				}
				childrenList.add(treeItemMap) ;
			}else{
				//存储根节点数据
				resultMap.add(treeItemMap) ;
			}
			//移除id和parentid字段信息
			if(isRemoveIdField){
				treeItemMap.remove(idField) ;
			}

			if(isRemoveParentIdField){
				treeItemMap.remove(parentIdField) ;
			}
		}

		return resultMap ;
	}
	
}
