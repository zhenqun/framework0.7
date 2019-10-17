package com.fosung.framework.common.secure.signature.strategy;

import com.fosung.framework.common.secure.signature.entity.SignatureEntity;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 通过工厂获取Web对应的签名策略
 */
@Slf4j
public abstract class AppSignatureStrategyFactory {

    private static List<AppSignatureStrategy> signatureStrategies = Lists.newArrayList() ;

    static {
        // 添加默认的一个签名策略
        addSignatureStrategy(new AppMD5AppSignatureStrategy());
    }

    /**
     * 添加签名策略
     */
    public static void addSignatureStrategy(AppSignatureStrategy appSignatureStrategy) {
        Assert.notNull(appSignatureStrategy, "签名策略不能为空");

        if( signatureStrategies.contains(appSignatureStrategy) ){
            return ;
        }

        signatureStrategies.add(appSignatureStrategy);
    }

    /**
     * 获取指定的签名策略
     * @param signatureEntity
     * @return
     */
    public static AppSignatureStrategy getAppSignatureStrategy(SignatureEntity signatureEntity ) {

        for (AppSignatureStrategy appSignatureStrategy : signatureStrategies) {
            if( appSignatureStrategy.support( signatureEntity ) ){
                return appSignatureStrategy;
            }
        }

        return null ;

    }

}
