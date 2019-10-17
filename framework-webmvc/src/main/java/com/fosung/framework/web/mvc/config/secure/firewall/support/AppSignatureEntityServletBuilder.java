package com.fosung.framework.web.mvc.config.secure.firewall.support;

import com.fosung.framework.common.secure.signature.entity.AppSignatureHttpHeaderConstant;
import com.fosung.framework.common.secure.signature.entity.SignatureEntityBuilder;
import com.fosung.framework.common.secure.signature.service.SignatureKeyService;
import com.fosung.framework.web.util.UtilWebSign;

import javax.servlet.http.HttpServletRequest;

/**
 * @author toquery
 * @version 1
 */
public class AppSignatureEntityServletBuilder extends SignatureEntityBuilder {

    private AppSignatureEntityServletBuilder() {
        super();
    }

    public static AppSignatureEntityServletBuilder newBuilder(SignatureKeyService signatureKeyService ) {
        SignatureEntityBuilder.signatureKeyService = signatureKeyService ;
        return new AppSignatureEntityServletBuilder();
    }

    public SignatureEntityBuilder request(HttpServletRequest request) {
        //客户端签名的值
        super.signature(UtilWebSign.getRequestHeaderValue(request, AppSignatureHttpHeaderConstant.X_CA_SIGNATURE));

        //签名key、随机数、时间戳 和 认证类型
        super.appKey( UtilWebSign.getRequestHeaderValue(request, AppSignatureHttpHeaderConstant.X_CA_KEY) ) ;
        super.nonce( UtilWebSign.getRequestHeaderValue(request, AppSignatureHttpHeaderConstant.X_CA_NONCE) ) ;
        super.timestamp( UtilWebSign.getRequestHeaderTimestamp(request, AppSignatureHttpHeaderConstant.X_CA_TIMESTAMP) ) ;
        super.authType( UtilWebSign.getRequestHeaderValue(request, AppSignatureHttpHeaderConstant.X_CA_AUTH_TYPE) ) ;

        //签名类型和版本
        super.signatureType(UtilWebSign.getRequestHeaderValue(request, AppSignatureHttpHeaderConstant.X_CA_SIGNATURE_TYPE));
        super.signatureVersion(UtilWebSign.getRequestHeaderValue(request, AppSignatureHttpHeaderConstant.X_CA_VERSION));

        //请求的uri、方法 以及参数
        super.requestUri(request.getRequestURI()) ;
        super.requestMethod(request.getMethod()) ;
        super.requestParamValue(request.getParameterMap()) ;
        return this;
    }

}
