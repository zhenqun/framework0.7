package com.fosung.framework.web.mvc.config.secure.firewall;

import com.alibaba.fastjson.JSON;
import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.util.UtilDevice;
import com.fosung.framework.web.util.UtilWeb;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

/**
 * 验证http referer是否有效
 * @Author : liupeng
 * @Date : 2018-10-16
 * @Modified By
 */
@Slf4j
public class AppHttpRefererFirewall extends AppFirewallAdaptor {

    protected AntPathMatcher patternMatcher = new AntPathMatcher();

    private AppSecureProperties.RefererConfig refererConfig = null ;

    /**
     * 默认允许的域名或ip地址
     */
    private Set<String> allowedDomain = Sets.newHashSet("localhost", "127.0.0.1", "*fosung.com" , "*dtdjzx.gov.cn", "*rkzzfdj.gov.cn");

    public AppHttpRefererFirewall(ApplicationContext applicationContext , AppSecureProperties appSecureProperties ) {
        super( applicationContext , appSecureProperties ) ;

        this.refererConfig = appSecureProperties.getReferer() ;

        this.addAllowedDomainPattern( refererConfig.getHosts() ) ;

        log.info("referer 配置: {}" , JsonMapper.toJSONString( appSecureProperties.getReferer() )) ;
    }

    @Override
    public boolean isEnable(HttpServletRequest request)  {
        return appSecureProperties.getReferer().isEnable() ;
    }

    @Override
    public FirewalledRequest doWrapRequest(HttpServletRequest request ) {
        if( isIgnoreRequest(request) || isAllowedDomain(request) ){
            return emptyFirewalledRequest( request ) ;
        }

        log.error("请求 {} referer来源 {} 无效", request.getRequestURI(), UtilWeb.getRequestReferer(request)) ;

        throw new RequestRejectedException( "请求 "+ request.getRequestURI() +" 的referer来源: "+ UtilWeb.getRequestReferer(request)  +" 无效" ) ;
    }

    /**
     * 根据当前请求的信息判断：当前请求是否为被忽略的请求。判断规则为：<br>
     * 1.如果是非post请求、文件上传请求，则默认忽略referer验证.<br>
     * 2.如果是post请求，则判断是否为允许设备类型发出的请求，默认允许的设备类型为手机和平板。
     */
    protected boolean isIgnoreRequest(HttpServletRequest request) {
        boolean flag = false;
        //验证请求方法和文件上传类型
        if (ServletFileUpload.isMultipartContent(request) || !StringUtils.equalsIgnoreCase("post", request.getMethod())) {
            flag = true;
        } else {
            //验证设备类型
            flag = UtilDevice.isEnabled(request, appSecureProperties.getAllowedDevices());
        }

        // 不对指定的url进行过滤
        if( !flag ){
            for (String excludeUri : refererConfig.getExcludeUris()) {
                if( !flag ){
                    flag = patternMatcher.match( excludeUri , request.getRequestURI() ) ;
                }
            }
        }

        log.debug("referer请求 {} {} , 是否被忽略:{}", request.getRequestURI(), request.getMethod(), flag);

        return flag;
    }

    /**
     * 判断referer是否为有效
     */
    public boolean isAllowedDomain(HttpServletRequest request) {
        //获取请求的domain
        String requestDomain = UtilWeb.getRequestSourceDomain(UtilWeb.getRequestReferer(request));

        if (UtilString.isBlank(requestDomain)) {
            log.error("referer请求源: null , 验证结果: false");
            return false;
        }

        //匹配请求域名，只要有一个满足条件即可
        String matchedPattern = "";

        Optional<String> anyAllowerDomainOptional = allowedDomain.stream().filter(item -> patternMatcher.match(item, requestDomain)).findAny();
        boolean flag = anyAllowerDomainOptional.isPresent();
        if (flag) {
            matchedPattern = anyAllowerDomainOptional.get();
        }
        log.debug("referer请求源:{} , 验证格式:{} , 验证结果:{}", requestDomain, matchedPattern, flag);

        return flag;
    }

    /**
     * 添加允许的domain，支持ant形式
     */
    public void addAllowedDomainPattern(String allowedDomainPattern) {
        if (Strings.isNullOrEmpty(allowedDomainPattern)) {
            return;
        }
        log.info("添加referer允许的domain:{}", allowedDomainPattern);
        allowedDomain.add(allowedDomainPattern.trim());
    }

    private void addAllowedDomainPattern(Set<String> hosts) {
        if (CollectionUtils.isEmpty(hosts)) {
            return;
        }
        log.info("添加referer允许的domain:{}", JSON.toJSONString(hosts));
        allowedDomain.addAll(hosts);
    }

}
