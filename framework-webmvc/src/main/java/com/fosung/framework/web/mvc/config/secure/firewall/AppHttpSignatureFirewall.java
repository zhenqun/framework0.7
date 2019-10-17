package com.fosung.framework.web.mvc.config.secure.firewall;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.secure.signature.annotation.APISignature;
import com.fosung.framework.common.secure.signature.entity.SignatureEntity;
import com.fosung.framework.common.secure.signature.service.SignatureKeyService;
import com.fosung.framework.common.secure.signature.service.SignatureService;
import com.fosung.framework.web.mvc.config.secure.firewall.support.AppSignatureEntityServletBuilder;
import com.fosung.framework.web.mvc.config.web.support.AppRequestMappingInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基于签名的防火墙检验
 * @Author : liupeng
 * @Date : 2018-10-17
 * @Modified By
 */
@Slf4j
public class AppHttpSignatureFirewall extends AppFirewallAdaptor {

    private AtomicBoolean inited = new AtomicBoolean(false);

    private AppRequestMappingInfoService requestMappingInfoService ;

    private SignatureService signatureService = null;

    private SignatureKeyService signatureKeyService = null;

    private AppSecureProperties.Signature signature = null;

    public AppHttpSignatureFirewall(ApplicationContext applicationContext , AppSecureProperties appSecureProperties) {
        super( applicationContext , appSecureProperties ) ;
        signature = appSecureProperties.getSignature() ;

    }

    @Override
    public boolean isEnable(HttpServletRequest request) {
        return signature.isEnable() ;
    }

    /**
     * 初始参数
     */
    private void initBean() {
        if (inited.get()) {
            return ;
        }
        // 映射信息获取
        requestMappingInfoService = getApplicationContext().getBean( AppRequestMappingInfoService.class ) ;

        //签名校验服务
        signatureService = applicationContext.getBean( SignatureService.class ) ;

        //签名key获取secret服务
        signatureKeyService = applicationContext.getBean( SignatureKeyService.class ) ;

        inited.set(true) ;
    }

    @Override
    public FirewalledRequest doWrapRequest(HttpServletRequest request) {
        // 初始化系统属性 和 配置
        initBean( ) ;

        try{
            HandlerMethod handlerMethod = requestMappingInfoService.getHandlerMethod( request ) ;
            // 只对handlerMethod进行校验
            if( handlerMethod!=null ){
                //获取添加的签名信息，签名信息为空，则不进行签名校验
                APISignature apiSignature = getAPISignature(handlerMethod.getMethod());
                if (apiSignature != null && apiSignature.enable()) {
                    // 构建签名实体
                    SignatureEntity signatureEntity = AppSignatureEntityServletBuilder.newBuilder( signatureKeyService )
                            .request( request )
                            .signatureConfig( signature )
                            .build() ;

                    if( !signatureService.isValidSignature( signatureEntity ) ) {
                        throw new RequestRejectedException( "请求 "+ request.getRequestURI() +" 的签名无效" ) ;
                    }
                }
            }
        }catch( Exception e ){
            e.printStackTrace();
        }

        return emptyFirewalledRequest( request ) ;
    }

    /**
     * 获取签名注解
     */
    private APISignature getAPISignature(Method method) {
        // 由方法获取注解
        APISignature APISignature = AnnotationUtils.findAnnotation(method, APISignature.class);

        // 由声明类获取注解
        if ( APISignature == null ) {
            APISignature = AnnotationUtils.findAnnotation( method.getDeclaringClass(), APISignature.class );
        }

        return APISignature;
    }

}
