package com.fosung.framework.common.secure.signature.detector;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.secure.signature.entity.SignatureEntity;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 在签名值正确后，校验签名是否被重复使用
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class SignatureUsedDetector extends AbstractSignatureDetector {

    private static final String REDIS_SIGNATRUE_PATH = "app:signature:";

    private static final String USED_SIGN_VALUE = "1";

    private StringRedisTemplate stringRedisTemplate;

    private static final String DETECTOR_NAME = "签名重复";

    public SignatureUsedDetector(ApplicationContext applicationContext) {
        super(DETECTOR_NAME);
        this.stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
    }

    /**
     * 签名是否已经使用过
     */
    public boolean isSignatureUsed(String signature, String requestURI, AppSecureProperties.Signature signatureConfig) {
        boolean signatureUsed = stringRedisTemplate.hasKey(REDIS_SIGNATRUE_PATH + signature);

        //如果签名没使用过，则存储签名，默认为8小时
        if (!signatureUsed) {
            stringRedisTemplate.opsForValue().set( REDIS_SIGNATRUE_PATH + signature,
                    USED_SIGN_VALUE, signatureConfig.getReuseTime(), TimeUnit.MINUTES );
        }

        return signatureUsed;
    }

    @Override
    public boolean detect(SignatureEntity signatureEntity) {
        String signature = signatureEntity.getSignature();

        String requestURI = signatureEntity.getRequestUri();

        boolean used = isSignatureUsed( signature, requestURI, signatureEntity.getSignatureConfig() ) ;

        if (used) {
            log.error("{}的签名{}已使用过,不能重复使用", requestURI, signature);
        }

        //没有使用过，则继续执行流程
        return !used;
    }

}
