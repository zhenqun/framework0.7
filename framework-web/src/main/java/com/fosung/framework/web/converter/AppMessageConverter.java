package com.fosung.framework.web.converter;

@Deprecated
public interface AppMessageConverter {

    /**
     * 是否支持当前对象的转换
     * @param obj
     * @return
     */
    boolean support(Object obj) ;

    /**
     * 将obj转换为json文本
     * @param obj
     * @return
     */
    String toJSONText( Object obj ) ;

}
