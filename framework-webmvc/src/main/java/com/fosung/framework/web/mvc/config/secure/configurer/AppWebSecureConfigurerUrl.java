package com.fosung.framework.web.mvc.config.secure.configurer;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.web.mvc.config.secure.configurer.support.UrlAuthDetail;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

/**
 * 一般url访问策略
 */
@Slf4j
public class AppWebSecureConfigurerUrl extends AppWebSecureConfigurerAdaptor {

    /**
     * 不同url格式之间的分隔符
     */
    public static final String URL_PATTERN_SPLITTER  = "," ;

    @Override
    public boolean isEnable() {
        // 是否允许执行权限验证
        log.info("执行权限验证:{}" , JsonMapper.toJSONString( appSecureProperties.getAuth().getUrlAuths() ));
        return appSecureProperties.getAuth().getUrlAuths() != null &&
                appSecureProperties.getAuth().getUrlAuths().size() > 0 ;
    }

    /**
     * web安全控制
     * @param httpSecurity
     */
    @Override
    public void doConfigure(HttpSecurity httpSecurity) throws Exception {

        log.info("{} 配置安全策略" , getClass().getSimpleName());

        AppSecureProperties.AuthConfig authConfig = getAppSecureProperties().getAuth() ;

        if( authConfig.getUrlAuths()==null || authConfig.getUrlAuths().isEmpty() ){
            log.warn("没有指定url的安全策略，默认所有的url只能登陆的用户才允许访问") ;
            httpSecurity.authorizeRequests().antMatchers("/**").authenticated() ;
            return ;
        }

        Set<String> existUrlPatterns = Sets.newHashSet() ;

        authConfig.getUrlAuths().stream().forEach( urlAuth -> {
            Assert.hasText( urlAuth.getFilter() , "基于url过滤的filter不能为空" ) ;

            // 获取url指定的filter及参数
            List<UrlAuthDetail> urlAuthDetails = UrlAuthDetail.buildUrlAuthDetails( urlAuth ) ;

            // 需要认证的url
            List<String> urlPatterns = Splitter.on( URL_PATTERN_SPLITTER ).omitEmptyStrings().trimResults()
                    .splitToList( urlAuth.getUrl() ) ;

            // 添加不同url的认证
            for ( String urlPattern : urlPatterns ) {
                // 针对 url 配置
                if( existUrlPatterns.contains( urlPattern ) ){
                    throw new IllegalArgumentException("相同url: "+urlPattern+" 安全认证只能配置一次。") ;
                }
                existUrlPatterns.add( urlPattern ) ;

                urlAuthDetails.stream().forEach( urlAuthDetail -> {
                    try {

                        switch (urlAuthDetail.appSecureFilter){
                            case anon : {
                                log.info("{} 不添加权限认证" , urlPattern) ;
                                httpSecurity.authorizeRequests().antMatchers( urlPattern ).permitAll() ;
                                break;
                            }

                            case logineduser : {
                                log.info("{} 登录的用户可访问" , urlPattern) ;
                                httpSecurity.authorizeRequests().antMatchers(urlPattern).authenticated() ;
                                break;
                            }

                            case roles : {
                                Assert.notEmpty( urlAuthDetail.appSecureFilterParams ,  urlPattern + "基于角色的认证的filter配置必须包含角色名称"); ;
                                log.info("{} 访问需要的角色为 {}" , urlPattern, Joiner.on(",").join( urlAuthDetail.appSecureFilterParams ) ) ;
                                httpSecurity.authorizeRequests().antMatchers(urlPattern).hasAnyRole( urlAuthDetail.appSecureFilterParams.toArray( new String[]{} ) ) ;
                                break;
                            }

                        }

                    } catch (Exception e) {
                        log.error("配置url的安全策略异常" , e);
                        e.printStackTrace();
                    }

                } );
            }

        } );

    }


}
