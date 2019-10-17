package com.fosung.framework.common.secure.signature.strategy;

import com.fosung.framework.common.secure.signature.entity.SignatureEntity;
import com.fosung.framework.common.util.UtilString;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.util.*;

@Slf4j
public abstract class AppAppSignatureStrategyAdaptor implements AppSignatureStrategy {

    /**
     * 签名过滤的字符
     */
    protected List<String> signatureEscapes = Lists.newArrayList(" ", "\\+");

    /**
     * 基于web请求的签名加密
     */
    @Override
    public String signature(SignatureEntity signatureEntity) {
        //获取签名需要的文本
        List<String> signatureValues = getSignatureValues(signatureEntity);
        //加密后返回
        return signature(signatureEntity , signatureValues);
    }

    /**
     * 获取签名的文本
     */
    protected List<String> getSignatureValues(SignatureEntity signatureEntity) {
        List<String> signatureValues = Lists.newArrayList();

        //添加signatureKey
        signatureValues.add(signatureEntity.getAppKey()) ;
        signatureValues.add(signatureEntity.getAppSecret()) ;
        //添加签名时间
        signatureValues.add(signatureEntity.getTimestamp() + "") ;
        //添加随机数
        signatureValues.add(signatureEntity.getNonce()) ;
        // 签名请求地址
        signatureValues.add(signatureEntity.getRequestUri()) ;
        //添加请求参数
        String paramStr = encryptRequestParam(signatureEntity.getRequestParamValue()) ;

        signatureValues.add(paramStr);

        return signatureValues;
    }

    //将request param 排序 MD5 加密
    protected String encryptRequestParam(Map<String, String[]> sourceParamMap) {
        // 存储按照key排序后的map
        Map<String, String> sortedParamMap = new TreeMap<>();
        // 请求参数名称
        Set<Map.Entry<String, String[]>> sourceParams = sourceParamMap.entrySet();
        for (Map.Entry<String, String[]> param : sourceParams) {
            // 获取请求参数对应的值，并按照自然序排列
            List<String> valueList = Arrays.asList(param.getValue());
            sortSignatureValues(valueList);

            // 对值使用逗号连接成一个字符串
            String encryptStr = StringUtils.join(valueList, SIGNATURE_VALUE_SPLITTER);

            sortedParamMap.put(param.getKey(), encryptStr);
        }

        // 将请求参数列表连接构成一个查询字符串
        StringBuilder str = new StringBuilder();
        for (String paramName : sortedParamMap.keySet()) {
            str.append(paramName).append( "=" ).append( sortedParamMap.get(paramName) ).append("&") ;
        }
        String requestParamStr = str.length()>0 ? str.substring(0, str.length() - 1) : "" ;

        return UtilString.isNotBlank(requestParamStr) ? DigestUtils.md5DigestAsHex(requestParamStr.getBytes()) : "" ;

    }

    public String signature(SignatureEntity signatureEntity , List<String> signatureValues) {
        Assert.isTrue(CollectionUtils.isNotEmpty(signatureValues), "签名的值列表不能为空");
        //过滤filter
        signatureValues = getFilteredValues(signatureValues);
        //将对需要签名的值进行排序
        sortSignatureValues(signatureValues);
        //获取签名文本
        String signatureText = getSignatureText(signatureValues);
        //加密后返回
        return encrypt(signatureEntity , signatureText);
    }

    /**
     * 过滤签名值
     */
    protected List<String> getFilteredValues(List<String> signatureValues) {
        List<String> filteredValues = Lists.newArrayListWithCapacity(signatureValues.size());
        for (String signatureValue : signatureValues) {
            //如果签名值为空，则不作为签名值的一部分
            if (StringUtils.isBlank(signatureValue)) {
                continue;
            }
            filteredValues.add(signatureValue);
        }
        return filteredValues;
    }

    /**
     * 对签名值进行排序
     */
    protected void sortSignatureValues(List<String> signatureValues) {
        //对values的值按照字母序进行正序排列
        Collections.sort(signatureValues);
    }

    /**
     * 获取需要进行签名的文本
     */
    protected String getSignatureText(List<String> signatureValues) {
        //获取签名参数
        String params = Joiner.on(SIGNATURE_VALUE_SPLITTER).join(signatureValues).toLowerCase() ;
        //过滤无效字符，例如空格和+
        params = escapeSignatureValue(params);
        log.debug("签名文本:{}", params);
        //转换unicode，避免不同语言的不同
        String unicodeText = unicode(params);
        log.debug("签名文本unicode:{}", unicodeText);
        return unicodeText;
    }

    /**
     * 转换签名值，过滤特殊字符
     */
    private String escapeSignatureValue(String signatureValue) {
        if (signatureValue == null) {
            return null;
        }
        //执行过滤
        for (String signatureEscape : signatureEscapes) {
            signatureValue = signatureValue.replaceAll( signatureEscape, "" ) ;
        }
        return signatureValue;
    }

    /**
     * 转换unicode
     */
    protected String unicode(String str) {
        int lenth = str.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lenth; i++) {
            int code = str.charAt(i);
            String s = Integer.toHexString(code);
            sb.append("\\u");
            for (int j = s.length(); j < 4; j++) {
                sb.append(0);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * 对签名文本进行加密
     * @param signatureEntity
     * @param signatureText
     * @return
     */
    public abstract String encrypt(SignatureEntity signatureEntity , String signatureText) ;

}
