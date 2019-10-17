package com.fosung.framework.common.secure.signature.strategy;

import com.fosung.framework.common.secure.signature.entity.SignatureEntity;

public interface AppSignatureStrategy {
    /**
     * 签名值的连接符
     */
    String SIGNATURE_VALUE_SPLITTER = ",";

    /**
     * 是否支持当前签名
     * @param signatureEntity
     * @return
     */
    boolean support( SignatureEntity signatureEntity ) ;

    /**
     * 执行签名生成签名字符串
     * @param signatureEntity
     * @return
     */
    String signature(SignatureEntity signatureEntity);

}
