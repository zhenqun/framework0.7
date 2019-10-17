package com.fosung.framework.common.secure.signature.entity;

import com.fosung.framework.common.config.AppSecureProperties;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author toquery
 * @version 1
 */
@Getter
@Setter
@Slf4j
public class SignatureEntity {

    //appKey
    private String appKey;

    //appSecret
    private String appSecret;

    //密文
    private String signature;

    //请求时间
    private Long timestamp;

    //随机值
    private String nonce;

    //签名类型 MD5
    private String signatureType;

    //签名版本号
    private String signatureVersion;

    //签名认证类型
    private SignatureAuthType authType = SignatureAuthType.ACCESSKEY;

    //请求uri
    private String requestUri;

    //请求方法
    private String requestMethod;

    private Map<String, String[]> requestParamValue = Maps.newHashMap();

    private AppSecureProperties.Signature signatureConfig;


}
