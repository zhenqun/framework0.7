package com.fosung.framework.common.secure.signature.service;

import com.fosung.framework.common.secure.signature.entity.SignatureEntity;

import java.security.SignatureException;

/**
 * 签名服务，检验签名是否正确
 * @Author : liupeng
 * @Date : 2018-10-17
 * @Modified By
 */
public interface SignatureService {
    /**
     * 是否具有有效的签名
     * @param signatureEntity
     * @return
     * @throws SignatureException
     */
    boolean isValidSignature(SignatureEntity signatureEntity) throws SignatureException;
}
