package com.fosung.framework.web.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fosung.framework.common.json.FastJsonDefaultValueFilter;
import com.fosung.framework.common.util.UtilReflection;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.http.ResponseParam;
import com.fosung.framework.web.common.interceptor.support.AppServletContextHolder;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Deprecated
@Slf4j
public class JSONPMessageConverter extends FastJsonHttpMessageConverter {

    private FastJsonDefaultValueFilter fastJsonDefaultValueFilter = new FastJsonDefaultValueFilter() ;

    private Set<AppMessageConverter> bindedAppMessageConverters = Sets.newLinkedHashSet() ;

    public void addJSONMessageConverter( List<AppMessageConverter> appMessageConverters) {
        if( appMessageConverters == null ){
            return;
        }

        for (AppMessageConverter appMessageConverter : appMessageConverters) {
            log.info("添加自定义json转换器: {}" , appMessageConverter.getClass().getName()) ;
            bindedAppMessageConverters.add( appMessageConverter ) ;
        }
    }

    /**
     * fastjson不支持接口类型的转换
     * @param clazz
     * @return
     */
    @Override
    protected boolean supports(Class<?> clazz) {
//        if(clazz.isInterface()){
//            log.warn("{} 不支持接口类型的方法参数转换，请使用实体类。" , this.getClass().getName()) ;
//            return false ;
//        }
        return super.supports(clazz) ;
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {

        String text = null ;

        for (AppMessageConverter bindedAppMessageConverter : bindedAppMessageConverters) {
            if( text != null ){
                break;
            }

            if( bindedAppMessageConverter.support( obj ) ){
                text = bindedAppMessageConverter.toJSONText( obj ) ;
            }
        }

        // 对没有转换的text进行处理
        if( text == null ){
            try {
                //如果是jackson构造的json数据，使用其自身的json转换方式
                if(obj!=null && "com.fasterxml.jackson.databind.node.ObjectNode".equalsIgnoreCase(obj.getClass().getName())){
                    text = obj.toString() ;
                }else if(obj!=null && "springfox.documentation.spring.web.json.Json".equalsIgnoreCase(obj.getClass().getName())){
                    text = UtilReflection.getMethods(obj.getClass(),
                            UtilReflection.withName("value")).toArray(new Method[]{})[0].invoke(obj).toString();
                }else{
                    text = JSON.toJSONString(obj , fastJsonDefaultValueFilter, super.getFeatures());
                }
            } catch (Exception e) {
                log.error("使用 {} 生成{}的json字符串数据失败 {}" , JSON.class.getName() , obj.getClass().getName() , e.getMessage() ) ;
            }

            //增加对jsonp协议的支持
            if(obj !=null && obj instanceof ResponseParam){
                ResponseParam responseParam = (ResponseParam) obj ;
                if(responseParam.isSupportJSONP()){
                    HttpServletRequest httpServletRequest = AppServletContextHolder.getHttpServletRequest() ;
                    //jsonp协议必须为get请求，且包括callback参数
                    if(httpServletRequest!=null && httpServletRequest.getMethod().equalsIgnoreCase("GET") &&
                            UtilString.isNotBlank(httpServletRequest.getParameter("callback"))){

                        text = httpServletRequest.getParameter("callback")+"("+text+")" ;

                        log.debug("JSONP请求返回结果->"+text);
                    }
                }
            }
        }

        byte[] bytes = text.getBytes(super.getCharset());

        outputMessage.getBody().write(bytes);
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        Object result = null ;
        try {
            result = super.readInternal(clazz , inputMessage) ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result ;
    }

}
