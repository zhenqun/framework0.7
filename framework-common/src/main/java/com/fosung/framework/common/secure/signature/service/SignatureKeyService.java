package com.fosung.framework.common.secure.signature.service;

/**
 * 根据签名的key获取对应的secret
 * @author toquery
 * @version 1
 */
public interface SignatureKeyService {

    /**
     * appKey, appSecret
     * 通过ak获取sk
     */
    String getSecretByKey(String key);
}
