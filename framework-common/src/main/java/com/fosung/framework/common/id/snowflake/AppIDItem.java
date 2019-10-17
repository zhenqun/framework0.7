package com.fosung.framework.common.id.snowflake;

/**
 * id生成器中每一部分
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public enum AppIDItem {
    timestamp("时间戳") ,
//    business("业务线或模块") , machineroom("机房") ,
    machine("每个机房中机器") , custom("自定义扩展") , num("每秒随机业务数字") ;

    public String remark ;

    AppIDItem(String remark ){
        this.remark = remark ;
    }
}