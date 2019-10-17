package com.fosung.framework.common.secure.signature.detector;


import com.fosung.framework.common.secure.signature.entity.SignatureEntity;

import java.security.SignatureException;

/**
 * 签名检测接口类
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public interface SignatureDetector {

    /**
     * 获取检测器名称
     * @return 返回检测器的名称
     */
    String getName();

    /**
     * 执行签名检测处理
     * @param signatureEntity 需要检查的签名实体
     * @return 允许返回true，否则false
     * @throws SignatureException 检测过程出现异常
     */
    boolean detect(SignatureEntity signatureEntity) throws SignatureException;

}
