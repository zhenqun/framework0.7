package com.fosung.framework.common.secure.signature.service;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.secure.signature.detector.*;
import com.fosung.framework.common.secure.signature.entity.SignatureEntity;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.security.SignatureException;
import java.util.Set;

@Slf4j
public class SignatureServiceImpl implements SignatureService , ApplicationContextAware , InitializingBean {

    private ApplicationContext applicationContext;

    @Autowired
    private AppSecureProperties appSecureProperties ;

    private Set<SignatureDetector> signatureDetectors = Sets.newLinkedHashSet();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext ;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("初始化spring bean {}", this.getClass().getSimpleName()) ;
        //对签名的参数进行检测
        signatureDetectors.add(new SignatureParamDetector()) ;

        //对签名的有效时间进行检测
        signatureDetectors.add(new SignatureTimestampDetector()) ;

        //对签名的值进行检测
        signatureDetectors.add(new SignatureValueDetector()) ;

        //对签名的重复性进行校验
        signatureDetectors.add(new SignatureUsedDetector(applicationContext)) ;
    }


    /**
     * 签名监测是否允许
     * @param uri
     * @param requestMethod
     * @return
     */
    public boolean isEnabled(String uri, String requestMethod) {
        //检查签名总开关是否打开
        if ( !appSecureProperties.getSignature().isEnable() ) {
            log.info("签名总开关已关闭");
            return false;
        }

        if ( ! appSecureProperties.getSignature().getSignatureMethods().contains( requestMethod.toLowerCase() ) ) {
            log.info("不过滤\"{}\"方法的请求{}", requestMethod , uri );
            return false;
        }

        // 可以增加其他排除条件

        return true;
    }

    @Override
    public boolean isValidSignature(SignatureEntity signatureEntity) throws SignatureException {
        //签名监测未开启，则默认所有的请求通过
        if ( !isEnabled( signatureEntity.getRequestUri() , signatureEntity.getRequestMethod() ) ) {
            return true;
        }

        // 暂时取消对签名类型的检查，默认为 ACCESSKEY
//        SignatureAuthType signatureAuthType = signatureEntity.getAuthType();
//
//        //对签名的有效性进行判断
//        Assert.notNull(signatureAuthType , "请求签名的类型不能为空，请检查签名配置方式与请求参数、session是否匹配");
//
//        Assert.isTrue(signatureAuthType == SignatureAuthType.ACCESSKEY, "请求签名的类型必须为ACCESSKEY ");

        boolean flag = true;

        for (SignatureDetector signatureDetector : signatureDetectors) {
            flag = signatureDetector.detect(signatureEntity);
            if (!flag) {
                log.error("{} {} 签名检测失败", signatureEntity.getRequestUri(), signatureDetector.getName());
                break;
            }
        }

        return flag;
    }



}
