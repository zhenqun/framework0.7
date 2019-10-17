package com.fosung.framework.web.mvc.config.web.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fosung.framework.common.util.UtilString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.TimeZone;

/**
 * 自定义的日期格式化处理
 * @Author : liupeng
 * @Date : 2018-12-05
 * @Modified By
 */
@Slf4j
public class AppJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public JsonFormat.Value findFormat(Annotated ann) {
        JsonFormat.Value value = super.findFormat(ann) ;

        // 没有找到JsonFormat注解 或 没有指定 pattern属性配置
        if( value == null || !value.hasPattern() ){
            DateTimeFormat dateTimeFormat = ann.getAnnotation(DateTimeFormat.class) ;
            // 使用 DateTimeFormat 注解格式化日期
            if( dateTimeFormat != null && UtilString.isNotBlank( dateTimeFormat.pattern() ) ){
                log.debug("找到dateTimeFormat，使用{}格式化日期" , dateTimeFormat.pattern() ) ;
                value = new JsonFormat.Value().withPattern(dateTimeFormat.pattern())
                        .withTimeZone(TimeZone.getTimeZone("GMT+8")) ;
            }
        }

        return value ;
    }
}
