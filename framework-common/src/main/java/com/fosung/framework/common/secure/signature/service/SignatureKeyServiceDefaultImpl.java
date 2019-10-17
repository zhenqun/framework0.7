package com.fosung.framework.common.secure.signature.service;

import com.fosung.framework.common.config.AppSecureProperties;
import com.fosung.framework.common.exception.SignatureInvalidException;
import com.fosung.framework.common.util.UtilString;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 默认签名secret获取方式
 * @Author : liupeng
 * @Date : 2018/8/14 17:07
 * @Modified By
 */
public class SignatureKeyServiceDefaultImpl implements SignatureKeyService {

    @Autowired
    private AppSecureProperties appSecureProperties ;

    @Override
    public String getSecretByKey(String key) {
        if( !UtilString.equalsIgnoreCase( key , appSecureProperties.getSignature().getAppKey() )){
            throw new SignatureInvalidException("签名请求key与配置的key=\""+appSecureProperties.getSignature().getAppKey()+"\"不相同") ;
        }

        return appSecureProperties.getSignature().getAppSecret() ;
    }

}
