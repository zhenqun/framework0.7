package com.fosung.framework.common.id;

import com.fosung.framework.common.id.snowflake.AppIDGenerator;

/**
 * id生成工具
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public abstract class IdGenerators<T> {

    public static final AppIDGenerator APP_ID_GENERATOR  = new AppIDGenerator() ;

    /**
     * 按照默认的id生成规则生成id
     * @return
     */
    public static Long getNextId(){
        return APP_ID_GENERATOR.getNextId() ;
    }

}
