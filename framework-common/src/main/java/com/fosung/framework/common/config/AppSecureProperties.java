package com.fosung.framework.common.config;

import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.util.UtilString;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author toquery
 * @version 1
 */
@Getter
@Setter
@ConfigurationProperties(prefix = AppSecureProperties.PREFIX )
public class AppSecureProperties {

    public static final String PREFIX = "app.secure" ;

    private Set<String> allowedDevices = Sets.newHashSet("MOBILE", "TABLET");

    private XSSConfig xss = new XSSConfig();

    private CORSConfig cors = new CORSConfig();

    private RefererConfig referer = new RefererConfig();

    private AuthConfig auth = new AuthConfig() ;

    private SSOConfig sso = new SSOConfig() ;

    private Signature signature = new Signature();

    @Getter
    @Setter
    public static class CORSConfig {
        //默认启用
        private boolean enable = true ;

        private int maxAge = 3600;

        private boolean allowCredentials = false;

        private Set<String> allowedOrigins = Sets.newHashSet("*");

        // 允许跨域的方式，只有get 和 post请求
        private Set<String> allowedMethods = Sets.newHashSet("GET", "POST" );

        private Set<String> allowedHeaders = Sets.newHashSet();

        private Set<String> exposedHeaders = Sets.newHashSet();
    }

    @Getter
    @Setter
    public static class RefererConfig {
        //默认启用
        private boolean enable = true ;

        private Set<String> hosts = Sets.newHashSet();

        // 例外url
        private Set<String> excludeUris = Sets.newHashSet( "/error" ) ;
    }

    @Slf4j
    @Getter
    @Setter
    public static class XSSConfig {

        //默认启用
        private boolean enable = true ;

        private Set<String> excludeUris = Sets.newHashSet() ;

        private Set<ExcludeParamConfig> excludeParams = Sets.newHashSet();

        public Map<String, List<String>> getExcludeParamsMap() {
            Map<String,List<String>> paramMap = Maps.newHashMap() ;

            for (ExcludeParamConfig excludeParam : excludeParams) {
                if( UtilString.isBlank(excludeParam.getUri()) || UtilString.isBlank(excludeParam.getParams())){
                    continue;
                }

                List<String> params = Splitter.on(",").trimResults().omitEmptyStrings().splitToList( excludeParam.getParams() ) ;

                paramMap.put( excludeParam.getUri() , params ) ;
            }

            log.info("xss过滤参数: {}" , JsonMapper.toJSONString( paramMap ));

            return paramMap ;
        }

        @Getter
        @Setter
        public static class ExcludeParamConfig {
            private String uri ;

            private String params = null ;

            @Override
            public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()){
                    return false;
                }
                if (this == o) {
                    return true;
                }
                ExcludeParamConfig that = (ExcludeParamConfig) o;

                return Objects.equals(uri, that.uri) &&
                        Objects.equals(params, that.params);
            }
            @Override
            public int hashCode() {
                return Objects.hash(uri, params);
            }
        }
    }

    @Getter
    @Setter
    public static class Signature {
        // 默认启用签名校验
        private boolean enable = true ;

        // 签名的有效时间，默认为60*8分钟，单位分钟
        private int validTime = 8 * 60;

        //签名可重复使用时间，单位分钟
        private int reuseTime = 8 * 60 ;

        private String appKey;

        private String appSecret;

        // 签名支持的请求方法
        private Set<String> signatureMethods = Sets.newHashSet("post") ;

//        private String appSecretBeanName;

    }

    @Getter
    @Setter
    public static class AuthConfig{
        /**
         * 本地系统用户
         */
        public static final String LOCAL_SYSTEM  = "local" ;

        private boolean enable = true ;

        // 请求的根地址或访问域名端口
        private String basePath = "" ;

        //登录地址
        private String loginUrl = "/login" ;

        //登录页面地址
        private String loginPageRedirectUrl = "/login" ;

        //登录时使用的用户名参数
        private String loginUsernameParam = "u" ;

        //登录时使用的密码参数
        private String loginPasswordParam = "p" ;

        //登录成功的地址
        private String successUrl = "/index" ;

        //未授权的地址
        private String unauthorizedUrl = "/unauthorized" ;

        //认证类型，本地系统用户(local) 或 灯塔接口用户(dt)，不同类型使用逗号分隔
        private String type = LOCAL_SYSTEM ;

        // 默认的用户角色，在新加用户后会默认添加
        private Set<String> defaultUserRoles = Sets.newHashSet() ;

        //url权限配置
        private List<UrlAuthConfig> urlAuths = Lists.newArrayList() ;

    }

    @Getter
    @Setter
    public static class SSOConfig{
        private boolean enable = false ;

        /**
         * 登录地址
         */
        private String loginUrl = "/oauth2/login" ;

        /**
         * 登录成功的访问地址
         */
        private String loginSuccessUrl = "/index" ;

        /**
         * 前端页面的访问地址
         */
        @Deprecated
        private String frontPageUrl = "http://localhost:9528/" ;

        @Deprecated
        private String clientId ;

        @Deprecated
        private String clientSecret ;

        @Deprecated
        private String clientName ;

        @Deprecated
        private String redirectUriTemplate = "{baseUrl}/oauth2/login" ;

        @Deprecated
        private String clientAuthenticationMethod = "post" ;

        @Deprecated
        private String authorizationGrantType = "authorization_code" ;

        @Deprecated
        private String authorizationUri ;

        @Deprecated
        private String tokenUri ;

        @Deprecated
        private String userInfoUri ;

        @Deprecated
        private String userNameAttributeName = "name" ;

        @Deprecated
        private String scope = "login" ;

        // oauth2认证配置
        private List<OAuth2Config> oauth2Configs = Lists.newArrayList() ;
    }

    @Getter
    @Setter
    public static class UrlAuthConfig {

        private String url ;

        private String filter ;
    }

    @Getter
    @Setter
    public static class OAuth2Config {

        // 登录地址注册id匹配对象
        private String registrationId = "" ;

        // 用户详情转换器
        private String userDetailsConverter = "" ;

        /**
         * 前端页面的访问地址
         */
        private String frontPageUrl = "http://localhost:9528/" ;

        private String clientId ;

        private String clientSecret ;

        private String clientName ;

        private String redirectUriTemplate = "{baseUrl}/oauth2/login" ;

        private String clientAuthenticationMethod = "post" ;

        private String authorizationGrantType = "authorization_code" ;

        private String authorizationUri ;

        private String tokenUri ;

        private String userInfoUri ;

        private String userNameAttributeName = "name" ;

        private String scope = "login" ;
    }
}
