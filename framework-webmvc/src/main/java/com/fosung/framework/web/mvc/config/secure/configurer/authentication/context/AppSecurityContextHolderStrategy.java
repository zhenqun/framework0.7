package com.fosung.framework.web.mvc.config.secure.configurer.authentication.context;

import com.fosung.framework.common.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

/**
 * 基于 Threadlocal 的 上下文请求上下文存储，使用 AppSecurityContextImpl 对象
 * @Author : liupeng
 * @Date : 2018-10-20
 * @Modified By
 */
@Slf4j
public class AppSecurityContextHolderStrategy implements
        SecurityContextHolderStrategy {

    private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();

    @Override
    public void clearContext() {
        contextHolder.remove();
    }

    @Override
    public SecurityContext getContext() {
        SecurityContext ctx = contextHolder.get();

        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }

        return ctx;
    }

    @Override
    public void setContext(SecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");

        if( context.getAuthentication()!=null && context.getAuthentication().getPrincipal()!=null ){
            log.debug("设置 SecurityContext = {} , principal = {} , {}" , context.getClass().getSimpleName() ,
                            context.getAuthentication().getPrincipal().getClass().getSimpleName(),
                    JsonMapper.toJSONString( context.getAuthentication().getPrincipal() ) ) ;
        }else{
            log.debug("设置 SecurityContext = {} , principal = null" , context.getClass().getSimpleName() , null ) ;
        }

        if( context instanceof AppSecurityContextImpl ){
            contextHolder.set(context);
        }else{
            AppSecurityContextImpl appSecurityContext = new AppSecurityContextImpl() ;
            // 设置认证实体实体
            appSecurityContext.setAuthentication( context.getAuthentication() ) ;
            contextHolder.set(appSecurityContext);
        }
    }

    /**
     * 创建空的请求上下文对象
     * @return
     */
    @Override
    public SecurityContext createEmptyContext() {
        log.debug("创建空的{}" , AppSecurityContextImpl.class.getSimpleName());
        return new AppSecurityContextImpl();
    }

}
