package com.fosung.framework.web.common.filter;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.json.JsonMapper;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 跨域filter请求处理类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
public class AppWebCorsFilter extends AppWebFilter {

    private AppSecureProperties.CORSConfig corsConfig;

    public AppWebCorsFilter(AppSecureProperties appSecureProperties) {
        this.corsConfig = appSecureProperties.getCors();

        log.info("cors 配置: {}" , JsonMapper.toJSONString( corsConfig ));
    }

    @Override
    public boolean shouldFilter(HttpServletRequest request) {
        return corsConfig.isEnable() ;
    }

    /**
     * 获取允许的跨域请求头
     */
    public String getAllowedHeaders() {
        return Joiner.on(",").join(corsConfig.getAllowedHeaders());
    }

    /**
     * 获取允许的跨域请求头
     */
    public String getAllowedMethods() {
        return Joiner.on(",").join(corsConfig.getAllowedMethods());
    }

    @Override
    public boolean doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.debug("{} 允许 跨域访问", httpServletRequest.getRequestURI());

        //设置http的请求头允许跨域访问
        String origin = httpServletRequest.getHeader("Origin");
        if (StringUtils.isNotBlank(origin)) {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        }
        if (corsConfig.getAllowedMethods().size() > 0) {
            httpServletResponse.setHeader("Access-Control-Allow-Methods", Joiner.on(",").join(corsConfig.getAllowedMethods()));
        }
        //设置http请求头允许签名开头的参数
        String allowedHeaders = getAllowedHeaders();
        if (StringUtils.isNotBlank(allowedHeaders)) {
            httpServletResponse.setHeader("Access-Control-Request-Headers", allowedHeaders);
            httpServletResponse.setHeader("Access-Control-Allow-Headers", allowedHeaders);
        }
        //允许请求头 withCredentials
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", String.valueOf(corsConfig.isAllowCredentials()));

        //统一拦截所有的OPTIONS请求，默认返回200
        //对于IE、Chrome中带有自定义Header的跨域请求，在发送真实请求之前，浏览器会发送嗅探请求，即OPTIONS请求
        //通过请求的返回的状态码，判断是否继续执行真实的请求
        if (httpServletRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }

        return true;
    }

}
