package com.fosung.framework.common.secure.signature.detector;

import com.fosung.framework.common.secure.signature.entity.SignatureEntity;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 对签名的Accessid进行检测
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class SignatureAccessIdDetector extends AbstractSignatureDetector {

    private static final String DETECTOR_NAME = "签名accessId检测";

    public SignatureAccessIdDetector() {
        super(DETECTOR_NAME);
    }

    @Override
    public boolean detect(SignatureEntity signatureEntity) {
        return true;
    }

}
