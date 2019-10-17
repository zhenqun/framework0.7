package com.fosung.framework.common.id.snowflake;

import java.util.Map;

/**
 * 记录id的每一部分组成
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public interface AppIDPart {

    /**
     * AppIDPart的名称
     * @return
     */
    String getName() ;

    /**
     * 设置id生成器中每部分id的长度
     * @param idItems
     */
    void setIdItems(Map<AppIDItem, Integer> idItems) ;

    /**
     * 按照秒生成唯一的随机数
     * @param binaryLength
     * @param timeSeconds
     * @param object
     * @return
     */
    long getNumIndex(int binaryLength, long timeSeconds, Object object) ;

    /**
     * 获取自定义字段内容索引
     * @param binaryLength
     * @param timeSeconds
     * @param object
     * @return
     */
    long getCustomIndex(int binaryLength, long timeSeconds, Object object) ;

    /**
     * 获取机器的标识，默认收集第三部分，<b>在docker中宿主机的ip默认是第三部分
     * @param binaryLength
     * @param timeSeconds
     * @param object
     * @return
     */
    long getMachineIndex(int binaryLength, long timeSeconds, Object object) ;

    /**
     * 获取时间索引
     * @param binaryLength
     * @param timeSeconds
     * @param object
     * @return
     */
    long getTimeIndex(Integer binaryLength, long timeSeconds, Object object) ;

}
