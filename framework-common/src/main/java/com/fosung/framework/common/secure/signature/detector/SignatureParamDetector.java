package com.fosung.framework.common.secure.signature.detector;

import com.fosung.framework.common.secure.signature.entity.SignatureEntity;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 检测签名参数是否符合规则
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class SignatureParamDetector extends AbstractSignatureDetector {

    private static final String DETECTOR_NAME = "签名参数检测";

    public SignatureParamDetector() {
        super(DETECTOR_NAME);
    }

    @Override
    public boolean detect(SignatureEntity signatureEntity) {
        long timestamp = signatureEntity.getTimestamp();
        if (timestamp == 0) {
            return false;
        }

        //获取随机数参数
        String nonce = signatureEntity.getNonce();
        if (StringUtils.isBlank(nonce)) {
            return false;
        }

        return true;
    }
}
