package com.fosung.framework.web.mvc.config.secure.configurer.support;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.mvc.config.secure.AppSecureFilter;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.util.Assert;

import java.util.List;

public class UrlAuthDetail {

    /**
     * 过滤器名称和参数 开始分隔符
     */
    public static final String FILTER_NAME_PARAM_START_SPLITTER  = "[" ;

    /**
     * 过滤器名称和参数 结束分隔符
     */
    public static final String FILTER_NAME_PARAM_END_SPLITTER  = "]" ;

    /**
     * 不同过滤器之间的分隔符
     */
    public static final String FILTER_SPLITTER  = ";" ;

    public String sourceFilterConfig ;

    public AppSecureFilter appSecureFilter;

    public List<String> appSecureFilterParams ;

    private UrlAuthDetail(){

    }

    /**
     * 构建基于url认证的详情 UrlAuthDetail
     * @param filterConfig
     * @return
     */
    public static UrlAuthDetail build(String filterConfig){
        UrlAuthDetail urlAuthDetail = new UrlAuthDetail() ;
        urlAuthDetail.sourceFilterConfig = filterConfig ;
        urlAuthDetail.appSecureFilter = getAppSecureFilter( filterConfig ) ;
        urlAuthDetail.appSecureFilterParams = getAppSecureFilterParams( filterConfig ) ;
        return urlAuthDetail ;
    }

    /**
     * 构建基于url filter的权限详情
     * @param urlAuthConfig
     * @return
     */
    public static List<UrlAuthDetail> buildUrlAuthDetails(AppSecureProperties.UrlAuthConfig urlAuthConfig ){
        Assert.hasText( urlAuthConfig.getFilter() , "必须制定url: "+urlAuthConfig.getUrl()+" 对应的过滤器" ) ;

        List<UrlAuthDetail> authDetails = Lists.newArrayList() ;

        Splitter.on( FILTER_SPLITTER ).omitEmptyStrings().trimResults().splitToList( urlAuthConfig.getFilter() )
                .stream().forEach( filterConfig->{
            UrlAuthDetail urlAuthDetail = build( filterConfig ) ;
            authDetails.add( urlAuthDetail ) ;
        } );

        return authDetails ;
    }

    /**
     * 获取过滤器名称
     * @param filterConfig
     * @return
     */
    private static AppSecureFilter getAppSecureFilter(String filterConfig){
        String filterName = filterConfig ;

        int filterStartIndex = filterConfig.indexOf( FILTER_NAME_PARAM_START_SPLITTER ) ;
        int filterEndIndex = filterConfig.lastIndexOf( FILTER_NAME_PARAM_END_SPLITTER ) ;

        if( filterStartIndex!=-1 && filterEndIndex!=-1 ){
            filterName = filterConfig.substring( 0 , filterStartIndex ).trim() ;
        }

        return AppSecureFilter.valueOf( filterName ) ;
    }

    /**
     * 获取过滤器参数
     * @param filterConfig
     * @return
     */
    private static List<String> getAppSecureFilterParams(String filterConfig){

        String filterParamText = null ;

        int filterStartIndex = filterConfig.indexOf("[") ;
        int filterEndIndex = filterConfig.lastIndexOf("]") ;

        if( filterStartIndex!=-1 && filterEndIndex!=-1 ){
            filterParamText = filterConfig.substring( filterStartIndex + 1 , filterEndIndex ).trim() ;
        }

        if( UtilString.isBlank( filterParamText ) ){
            return null ;
        }

        return Splitter.on(",").omitEmptyStrings().trimResults().splitToList( filterParamText ) ;
    }

    @Override
    public boolean equals(Object obj) {
        boolean flag = super.equals(obj);
        if( flag ){
            return true ;
        }
        UrlAuthDetail other = (UrlAuthDetail)obj ;

        return UtilString.equals( sourceFilterConfig , other.sourceFilterConfig ) ;
    }

    @Override
    public int hashCode() {
        return sourceFilterConfig.hashCode() ;
    }
}