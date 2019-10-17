package com.fosung.framework.web.mvc.config.secure.firewall;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.util.UtilCollection;
import com.fosung.framework.common.util.UtilString;
import com.google.common.collect.Maps;
import com.google.common.html.HtmlEscapers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.firewall.FirewalledRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基于xss的防火墙检验
 * @Author : liupeng
 * @Date : 2018-10-16
 * @Modified By
 */
@Slf4j
public class AppHttpXSSFirewall extends AppFirewallAdaptor {

    private AppSecureProperties.XSSConfig xssConfig ;

    private Set<String> excludeUris = null ;

    private Map<String, List<String>> excludeParams = Maps.newHashMap();

    public AppHttpXSSFirewall(ApplicationContext applicationContext , AppSecureProperties appSecureProperties) {
        super( applicationContext , appSecureProperties);
        this.xssConfig = appSecureProperties.getXss() ;
        this.excludeUris = appSecureProperties.getXss().getExcludeUris() ;
        this.excludeParams = appSecureProperties.getXss().getExcludeParamsMap();

        log.info("xss配置: {}" , JsonMapper.toJSONString( xssConfig ));
    }

    @Override
    public boolean isEnable(HttpServletRequest request) {
        if( !xssConfig.isEnable() ){
            return false ;
        }

        //没有指定查询参数
        if( !UtilCollection.sizeIsEmpty( excludeUris ) && excludeUris.contains( request.getRequestURI() )){
            return false ;
        }

        return true ;
    }

    @Override
    public FirewalledRequest doWrapRequest(HttpServletRequest httpServletRequest) {
        EscapedHttpServletRequest escapedHttpServletRequest = new EscapedHttpServletRequest(httpServletRequest) ;

        return new FirewalledRequest(escapedHttpServletRequest) {
            @Override
            public void reset() {

            }
        } ;
    }

    /**
     * 带转义的request请求
     */
    class EscapedHttpServletRequest extends HttpServletRequestWrapper {

        protected HttpServletRequest request;

        public EscapedHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public String getParameter(String name) {
            return escape(name, super.getParameter(name));
        }

        @Override
        public String[] getParameterValues(String name) {
            return escape(name, super.getParameterValues(name));
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> paramMap = super.getParameterMap();
            if (paramMap == null || paramMap.size() < 1) {
                return paramMap;
            }

            // 新建一个转换后的value值map
            Map<String, String[]> eacapedValueMap = Maps.newHashMapWithExpectedSize(paramMap.size()) ;
            for (Map.Entry<String, String[]> paramEntry : paramMap.entrySet()) {
                // paramEntry.setValue(escape( paramEntry.getKey() , paramEntry.getValue())) ;
                eacapedValueMap.put( paramEntry.getKey() , escape(paramEntry.getKey(), paramEntry.getValue())) ;
            }

            return eacapedValueMap;
        }

        /**
         * 对字符串进行转义
         */
        public String escape(String name, String value) {
            if ( UtilString.isNotBlank(value) ) {
                if( ( !UtilCollection.sizeIsEmpty( excludeUris ) && excludeUris.contains( request.getRequestURI() ) ) ||
                        ( !UtilCollection.sizeIsEmpty( excludeParams ) && excludeParams.containsKey(request.getRequestURI())
                                && excludeParams.get( request.getRequestURI() ).contains(name) ) ){
                    //是否需要值排除某一个请求中的参数
                    return value ;
                } else {
                    value = HtmlEscapers.htmlEscaper().escape(value) ;
                }
            }
            log.debug("转义请求参数：" + value);

            return value;
        }

        /**
         * 对字符数组进行转义
         */
        public String[] escape(String name, String[] values) {
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = escape(name, values[i]);
                }
            }

            return values;
        }

    }

}
