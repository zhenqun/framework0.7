package com.fosung.framework.common.secure.signature.detector;

import com.fosung.framework.common.secure.signature.entity.SignatureEntity;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

/**
 * 签名时间检测
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class SignatureTimestampDetector extends AbstractSignatureDetector {

    private static final String DETECTOR_NAME = "签名时间有效性检测";

    public SignatureTimestampDetector() {
        super(DETECTOR_NAME);
    }

    @Override
    public boolean detect(SignatureEntity signatureEntity) {
        //获取签名时间
        DateTime signatureTime = new DateTime( signatureEntity.getTimestamp() );

        //计算当前时间和签名时间的差
        long timeDiff = Math.abs(new DateTime().toDate().getTime() - signatureTime.toDate().getTime());

        if ( TimeUnit.MILLISECONDS.toMinutes( timeDiff ) > signatureEntity.getSignatureConfig().getValidTime()) {
            log.error("{}提交的签名时间为{}，超出服务器器时间{}分钟 ", signatureEntity.getRequestUri(), signatureTime.toString("yyyy-MM-dd HH:mm:ss"), signatureEntity.getSignatureConfig().getValidTime());
            return false;
        }

        return true;
    }

}
