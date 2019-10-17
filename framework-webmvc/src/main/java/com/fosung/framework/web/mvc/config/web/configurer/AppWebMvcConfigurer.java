package com.fosung.framework.web.mvc.config.web.configurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.web.common.interceptor.AppRequestContextInterceptor;
import com.fosung.framework.web.converter.AppMessageConverter;
import com.fosung.framework.web.mvc.config.secure.dataaccess.APIDataAccessPrincipalInterceptor;
import com.fosung.framework.web.mvc.config.web.json.AppJacksonAnnotationIntrospector;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * appmvc基础配置类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class AppWebMvcConfigurer implements WebMvcConfigurer {

    @Getter
    @Setter
    @Value("${spring.mvc.home:forward:/index}")
    private String homeIndex;

    @Autowired
    private AppSecureProperties appSecureProperties;

    @Getter
    @Setter
    private List<AppMessageConverter> appMessageConverters;

    /**
     * 系统上下文运行的参数
     */
//    @Setter
//    @Getter
//    @Value("${spring.mvc.contextParams:key=value}")
//    private Map<String,String> contextParams ;

    //需要添加自定义拦截器
    private Set<HandlerInterceptor> handlerInterceptors = Sets.newHashSet(
            //请求上下文拦截器
            new AppRequestContextInterceptor(),
            //数据访问拦截器
            new APIDataAccessPrincipalInterceptor()
    );

    public AppWebMvcConfigurer() {
        log.info("创建自定义的mvc配置 {}", this.getClass().getSimpleName());
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        AppSecureProperties.CORSConfig corsConfig = appSecureProperties.getCors();
        if( !corsConfig.isEnable() ){
            return;
        }
        log.info("启用跨域配置" + JsonMapper.toJSONString( corsConfig ));
        CorsRegistration corsRegistration = registry.addMapping("/**");
        if (corsConfig.getAllowedOrigins().size() > 0) {
            corsRegistration = corsRegistration.allowedOrigins(set2Array(corsConfig.getAllowedOrigins()));
        }
        if (corsConfig.getAllowedMethods().size() > 0) {
            corsRegistration = corsRegistration.allowedMethods(set2Array(corsConfig.getAllowedMethods()));
        }
        if (corsConfig.getAllowedHeaders().size() > 0) {
            corsRegistration = corsRegistration.allowedHeaders(set2Array(corsConfig.getAllowedHeaders()));
        }
        if (corsConfig.getExposedHeaders().size() > 0) {
            corsRegistration = corsRegistration.exposedHeaders(set2Array(corsConfig.getExposedHeaders()));
        }
        corsRegistration.allowCredentials(corsConfig.isAllowCredentials()).maxAge(corsConfig.getMaxAge());

    }

    private String[] set2Array(Set<String> stringSet) {
        String[] strings = new String[stringSet.size()];
        return stringSet.toArray(strings);
    }


    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        log.info("设置根路径访问试图: / = {}", homeIndex);
        //设置默认的访问试图
        registry.addViewController("/").setViewName(homeIndex);
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        handlerInterceptors.forEach(handlerInterceptor -> {
            log.info("添加自定义的拦截器: {}", handlerInterceptor.getClass().getName());
            registry.addInterceptor(handlerInterceptor);
        });

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        /*List<HttpMessageConverter> customHttpMessageConverters = Lists.newArrayList() ;
        //2进制数据处理
        customHttpMessageConverters.add(new ByteArrayHttpMessageConverter()) ;

        //默认使用utf-8的编码方式
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setWriteAcceptCharset(false);
        customHttpMessageConverters.add(stringConverter);

        //返回值内容类型为json，则转换为json格式，使用alibaba的fastjson
        JSONPMessageConverter jsonpMessageConverter = new JSONPMessageConverter() ;
        jsonpMessageConverter.addJSONMessageConverter( appMessageConverters );
        customHttpMessageConverters.add( jsonpMessageConverter ) ;

        log.info("清理系统自带的HttpMessageConverter");
        converters.clear();

        customHttpMessageConverters.forEach(httpMessageConverter -> {
            converters.add( httpMessageConverter ) ;
            log.info("添加HttpMessageConverter : {}" , httpMessageConverter.getClass().getName() );
        });*/

        for (HttpMessageConverter<?> httpMessageConverter : converters) {
            log.info("添加HttpMessageConverter : {}", httpMessageConverter.getClass().getName());
            if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
                ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter) httpMessageConverter).getObjectMapper();
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addSerializer(Long.class, ToStringSerializer.instance) ;
                simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance) ;
                objectMapper.registerModule(simpleModule);
                log.info("向MappingJackson2HttpMessageConverter添加Long类型转换规则");

                // 使用自定义的注解处理，对日期处理
                objectMapper.setAnnotationIntrospector( new AppJacksonAnnotationIntrospector() ) ;

            }
        }
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();

        validatorFactoryBean.setProviderClass(HibernateValidator.class);

        log.info("初始化表单实体验证,验证类提供者{}", HibernateValidator.class.getName());

        return validatorFactoryBean;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        //todo 设置自定义视图，暴露上下文属性

//        Method getViewResolver = ReflectionUtils.findMethod( urlRegistration.getClass() , "getViewResolver"  ) ;
//        getViewResolver.setAccessible( true ) ;
//        try {
//            UrlBasedViewResolver urlBasedViewResolver = (UrlBasedViewResolver) getViewResolver.invoke(
//                    urlRegistration ) ;
//            urlBasedViewResolver.setContentType("text/html;charset=UTF-8");
//            //暴漏requestContext , requestContext的实现类为org.springframework.web.servlet.task.RequestContext
//            urlBasedViewResolver.setRequestContextAttribute("requestContext") ;
//            urlBasedViewResolver.setAttributesMap( getContextParams() ) ;
//            //如果是抽象模板，暴漏request和session中的属性
//            if(urlBasedViewResolver instanceof AbstractTemplateViewResolver){
//                AbstractTemplateViewResolver abstractTemplateViewResolver = (AbstractTemplateViewResolver) urlBasedViewResolver ;
//                log.info("暴漏request和session的属性到请求上下文，在页面模板中可以获取request和session对象") ;
//                abstractTemplateViewResolver.setExposeRequestAttributes(true) ;
//                abstractTemplateViewResolver.setExposeSessionAttributes(true) ;
//                //允许session或request中的属性重写
//                abstractTemplateViewResolver.setAllowSessionOverride(true) ;
//                abstractTemplateViewResolver.setAllowRequestOverride(true) ;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
