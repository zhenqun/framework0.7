package com.fosung.framework.common.secure.signature.detector;

import com.fosung.framework.common.secure.signature.entity.SignatureEntity;
import com.fosung.framework.common.secure.signature.strategy.AppSignatureStrategy;
import com.fosung.framework.common.secure.signature.strategy.AppSignatureStrategyFactory;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.security.SignatureException;

/**
 * 检测签名的值是否正确，允许返回true，不允许返回false
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class SignatureValueDetector extends AbstractSignatureDetector {

    private static final String DETECTOR_NAME = "签名值正确性检测";

    public SignatureValueDetector() {
        super(DETECTOR_NAME);
    }

    /**
     * 生成后端签名
     */
    public String generateSignature(SignatureEntity signatureEntity) throws SignatureException {
        //根据签名类型 和 指定的版本 获取 对应的签名策略
        AppSignatureStrategy appSignatureStrategy = AppSignatureStrategyFactory.getAppSignatureStrategy( signatureEntity ) ;
        if (appSignatureStrategy == null) {
            throw new SignatureException("获取签名策略为" + signatureEntity.getSignatureType() + "的版本为" + signatureEntity.getSignatureVersion() + "失败");
        }

        return appSignatureStrategy.signature(signatureEntity);
    }

    /**
     * 签名是否有效
     *
     * @param signature 服务端生成的签名
     */
    public boolean isSignatureValid(String signature, String clientSignature) {
        return StringUtils.equalsIgnoreCase(signature, clientSignature);
    }

    @Override
    public boolean detect(SignatureEntity signatureEntity) throws SignatureException {
        //生成客户端签名
        String clientSignature = signatureEntity.getSignature();
        if (StringUtils.isBlank(clientSignature)) {
            log.error("{}请求没有带签名", signatureEntity.getRequestUri());
            return false;
        }
        //生成后端的签名
        String signature = generateSignature(signatureEntity);

        //验证签名是否正确
        boolean isValidSignature = isSignatureValid(signature, clientSignature);

        if (!isValidSignature) {
            log.error("{} 前后台生成的签名值不一致,后端签名{} , 前端签名{}", signatureEntity.getRequestUri(), signature, signatureEntity.getSignature());
        }

        return isValidSignature;
    }

}
