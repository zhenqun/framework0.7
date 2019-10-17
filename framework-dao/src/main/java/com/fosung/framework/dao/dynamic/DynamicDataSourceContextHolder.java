package com.fosung.framework.dao.dynamic;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author toquery
 * @version 1
 */
public class DynamicDataSourceContextHolder {
    /*
     * 当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /*
     * 管理所有的数据源id;
     * 主要是为了判断数据源是否存在;
     */
    public static Set<String> dataSourceIds = Sets.newHashSet();


    /**
     * 使用setDataSourceType设置当前的
     */
    public static void setDataSourceType(String dataSourceType) {
        contextHolder.set(dataSourceType);
    }


    public static String getDataSourceType() {
        return contextHolder.get();
    }


    public static void clearDataSourceType() {
        contextHolder.remove();
    }


    /**
     * 判断指定DataSrouce当前是否存在
     */
    public static boolean containsDataSource(String dataSourceId) {
        return dataSourceIds.contains(dataSourceId);
    }

    public static void addDataSourceIds(String dataSourceId) {
        DynamicDataSourceContextHolder.dataSourceIds.add(dataSourceId);
    }

    public static void addAllDataSourceIds(Set<String> allDataSourceIds) {
        DynamicDataSourceContextHolder.dataSourceIds.addAll(allDataSourceIds);
    }
}
