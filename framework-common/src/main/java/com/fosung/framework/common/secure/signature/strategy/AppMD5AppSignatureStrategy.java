package com.fosung.framework.common.secure.signature.strategy;

import com.fosung.framework.common.secure.signature.entity.SignatureEntity;
import com.fosung.framework.common.util.UtilString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

@Slf4j
public class AppMD5AppSignatureStrategy extends AppAppSignatureStrategyAdaptor {

    private static final String SIGNATURE_STRATEGY_NAME = "MD5";

    private static final String SIGNATURE_STRATEGY_VERSION = "1";

    @Override
    public boolean support(SignatureEntity signatureEntity) {
        return UtilString.equalsIgnoreCase( signatureEntity.getSignatureType() , SIGNATURE_STRATEGY_NAME ) &&
                UtilString.equalsIgnoreCase( signatureEntity.getSignatureVersion() , SIGNATURE_STRATEGY_VERSION );
    }

    /**
     * 对签名文本进行加密
     * @param signatureText
     * @return
     */
    @Override
    public String encrypt(SignatureEntity signatureEntity, String signatureText) {
        //对签名文本进行2次md5加密
        String signature = DigestUtils.md5DigestAsHex(signatureText.getBytes());
        signature = DigestUtils.md5DigestAsHex(signature.getBytes());
        log.debug("签名文本加密后:{}", signature);
        return signature;
    }

}
