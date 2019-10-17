package com.fosung.framework.web.common.filter;

import com.fosung.framework.common.util.UtilCollection;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author : liupeng
 * @Date : 2018/8/15 15:47
 * @Modified By
 */
@Slf4j
public class AppWebFilterDelegate extends OncePerRequestFilter {

    private List<AppWebFilter> appWebFilters = Lists.newArrayList() ;

    /**
     * 添加过滤器
     * @param appWebFilters
     */
    public void addAppWebFilters(List<AppWebFilter> appWebFilters){
        if(UtilCollection.isEmpty( appWebFilters )){
            return ;
        }
        this.appWebFilters.addAll( appWebFilters ) ;

        // 对于加入的所有filter按照Order接口进行排序
        AnnotationAwareOrderComparator.sort( this.appWebFilters ) ;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isContinue = true ;

        AppWebFilter lastRunAppWebFilter = null ;

        for (AppWebFilter appWebFilter : appWebFilters) {
            if( !appWebFilter.shouldFilter(request) ){
                log.debug("{} 不过滤请求 {}" , appWebFilter.getClass().getSimpleName() , request.getRequestURI()) ;
                continue;
            }

            // 记录最后一个执行的filter
            lastRunAppWebFilter = appWebFilter ;

            //指定完成过滤之后，是否继续执行
            isContinue = appWebFilter.doFilterInternal( request , response ) ;
            if( !isContinue ){
                break;
            }

            //封装request 和 response
            request = appWebFilter.wrapRequest( request ) ;
            response = appWebFilter.wrapResponse( response ) ;
        }

        if( isContinue ){
            filterChain.doFilter( request , response ) ;
        }else{
            log.error("{}.doFilterInternal() 执行返回false，不再继续执行后续方法" , lastRunAppWebFilter.getClass().getName() ) ;
        }

    }
}
