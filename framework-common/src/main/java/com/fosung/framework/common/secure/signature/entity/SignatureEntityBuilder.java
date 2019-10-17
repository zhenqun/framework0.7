package com.fosung.framework.common.secure.signature.entity;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.secure.signature.service.SignatureKeyService;
import com.fosung.framework.common.util.UtilString;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 签名实体构造器
 * @author toquery
 * @version 1
 */
public abstract class SignatureEntityBuilder {

    public static SignatureKeyService signatureKeyService ;

    private SignatureEntity signatureEntity = null;

    protected SignatureEntityBuilder() {
        signatureEntity = new SignatureEntity();
    }

//    public static SignatureEntityBuilder newBuilder() {
//        return new SignatureEntityBuilder();
//    }

    public SignatureEntityBuilder appKey(String appKey) {
        Assert.hasLength(appKey, "appKey 不能为空");
        signatureEntity.setAppKey(appKey);
        return this;
    }

    public SignatureEntityBuilder appSecret(String appSecret) {
        Assert.hasLength(appSecret, "appSecret 不能为空");
        signatureEntity.setAppSecret(appSecret);
        return this;
    }


    public SignatureEntityBuilder signature(String signature) {
        Assert.hasLength(signature, "signature 不能为空");
        signatureEntity.setSignature(signature);
        return this;
    }


    public SignatureEntityBuilder nonce(String nonce) {
        Assert.hasLength(nonce, "nonce 不能为空");
        signatureEntity.setNonce(nonce);
        return this;
    }

    public SignatureEntityBuilder signatureVersion(String signatureVersion) {
        Assert.hasLength(signatureVersion, "signatureVersion 不能为空");
        signatureEntity.setSignatureVersion(signatureVersion);
        return this;
    }

    public SignatureEntityBuilder timestamp(Long timestamp) {
        Assert.isTrue(timestamp != null, "timestamp 不能为空");
        signatureEntity.setTimestamp(timestamp);
        return this;
    }

    public SignatureEntityBuilder signatureType(String signatureType) {
        Assert.hasLength(signatureType, "signatureType 不能为空");
        signatureEntity.setSignatureType(signatureType);
        return this;
    }


    public SignatureEntityBuilder authType(SignatureAuthType signatureAuthType) {
        Assert.isTrue(signatureAuthType != null, "authType 不能为空");
        signatureEntity.setAuthType(SignatureAuthType.ACCESSKEY);
        return this;
    }

    public SignatureEntityBuilder authType( String authType ) {
        Assert.hasLength(authType, "authType 不能为空");

//        SignatureAuthType signatureAuthTypeEmun = StringUtils.equalsIgnoreCase(authType, "accesskey") ?
//                SignatureAuthType.ACCESSKEY : SignatureAuthType.ACCESSKEY;

        signatureEntity.setAuthType( SignatureAuthType.ACCESSKEY );

        return this;
    }

    public SignatureEntityBuilder requestUri(String requestUri) {
        Assert.hasLength(requestUri, "requestUri 不能为空");
        signatureEntity.setRequestUri(requestUri);
        return this;
    }

    public SignatureEntityBuilder requestMethod(String requestMethod) {
        Assert.hasLength(requestMethod, "requestMethod 不能为空");
        signatureEntity.setRequestMethod(requestMethod);
        return this;
    }

    public SignatureEntityBuilder requestParamValue(Map<String, String[]> requestParamValues) {
        // 取消校验请求参数
//        Assert.isTrue(requestParamValues != null && requestParamValues.size() > 0, "requestParamValues 不能为空");
        signatureEntity.setRequestParamValue(requestParamValues);
        return this;
    }

    public SignatureEntityBuilder signatureConfig(AppSecureProperties.Signature signatureConfig) {
        Assert.isTrue(signatureConfig != null, "signatureConfig 不能为空");
        signatureEntity.setSignatureConfig(signatureConfig);
        return this;
    }

    public final SignatureEntity build() {
        Assert.hasText(signatureEntity.getAppKey(), "appKey 不能为空");
        Assert.hasLength(signatureEntity.getSignature(), "signature 不能为空");
        Assert.hasLength(signatureEntity.getNonce(), "nonce 不能为空");
        Assert.isTrue(signatureEntity.getSignatureType() != null, "SignatureType 不能为空");
        Assert.isTrue(signatureEntity.getTimestamp() != null, "timestamp 不能为空");

//        Assert.isTrue(signatureEntity.getRequestParamValue() != null && signatureEntity.getRequestParamValue().size() > 0, "request param 不能为空");
        Assert.hasLength(signatureEntity.getSignatureVersion(), "signatureVersion 不能为空");
        Assert.hasLength(signatureEntity.getRequestUri(), "requestUri 不能为空");
        Assert.hasLength(signatureEntity.getRequestMethod(), "requestMethod 不能为空");

        //如果 appSecret 为空 , signatureKeyService 不为空则通过signatureKeyService获取 appSecret
        if(UtilString.isBlank(signatureEntity.getAppSecret()) && signatureKeyService!=null ){
            String appSecret = signatureKeyService.getSecretByKey( signatureEntity.getAppKey() ) ;
            signatureEntity.setAppSecret( appSecret );
        }

        Assert.hasText(signatureEntity.getAppSecret(), "appSecret 不能为空");

        return this.signatureEntity;
    }
}
