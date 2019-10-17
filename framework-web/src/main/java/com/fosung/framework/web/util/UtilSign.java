
package com.fosung.framework.web.util;

import com.fosung.framework.common.secure.signature.entity.AppSignatureHttpHeaderConstant;
import com.fosung.framework.common.util.UtilDigest;
import com.fosung.framework.web.util.support.HttpHeaderConstant;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * 签名工具类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilSign {

    /**
     * 换行符
     */
    public static final String DEFAULT_LN = "\n";

    public static final String DEFAULT_ALGORITHM = "HmacSHA256";


    /**
     * 编码UTF-8
     */
    public static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");

    /**
     * 签名方法
     * 本方法将Request中的httpMethod、headers、path、queryParam、formParam合成一个字符串用hmacSha256算法双向加密进行签名
     *
     * @param method         http method
     * @param appSecret      your app secret
     * @param headerParams   http headers
     * @param pathWithParams params builted in http path
     * @param queryParams    http query params
     * @param formParams     form params
     * @return signResults
     */
    public static String sign(String method, String appSecret, Map<String, String> headerParams, String pathWithParams, Map<String, String> queryParams, Map<String, String> formParams) throws Exception {
        try {
            Mac hmacSha256 = Mac.getInstance(DEFAULT_ALGORITHM);
            byte[] keyBytes = appSecret.getBytes(DEFAULT_ENCODING);
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, DEFAULT_ALGORITHM));

            //将Request中的httpMethod、headers、path、queryParam、formParam合成一个字符串
            String signString = combineParamsTogether(method, headerParams, pathWithParams, queryParams, formParams);

            //对字符串进行hmacSha256加密，然后再进行BASE64编码
            byte[] signResult = hmacSha256.doFinal(signString.getBytes(DEFAULT_ENCODING));

            // todo 删掉了shiro的Base64 , 改用UtilDigest , modified by liupeng
            return UtilDigest.encodeBase64( String.valueOf( signResult ) ) ;
//            return Base64.encodeToString(signResult);
        } catch (Exception e) {
            throw new Exception("创建签名失败.", e);
        }
    }

    /**
     * 将Request中的httpMethod、headers、path、queryParam、formParam合成一个字符串
     */
    private static String combineParamsTogether(String method, Map<String, String> headerParams, String pathWithParams, Map<String, String> queryParams, Map<String, String> formParams) {

        StringBuilder sb = new StringBuilder();
        sb.append(method).append(DEFAULT_LN);

        //如果有@"Accept"头，这个头需要参与签名
        if (headerParams.get(HttpHeaderConstant.HTTP_HEADER_ACCEPT) != null) {
            sb.append(headerParams.get(HttpHeaderConstant.HTTP_HEADER_ACCEPT));
        }
        sb.append(DEFAULT_LN);

        //如果有@"Content-MD5"头，这个头需要参与签名
        if (headerParams.get(HttpHeaderConstant.HTTP_HEADER_CONTENT_MD5) != null) {
            sb.append(headerParams.get(HttpHeaderConstant.HTTP_HEADER_CONTENT_MD5));
        }
        sb.append(DEFAULT_LN);

        //如果有@"Content-Type"头，这个头需要参与签名
        if (headerParams.get(HttpHeaderConstant.HTTP_HEADER_CONTENT_TYPE) != null) {
            sb.append(headerParams.get(HttpHeaderConstant.HTTP_HEADER_CONTENT_TYPE));
        }
        sb.append(DEFAULT_LN);

        //签名优先读取HTTP_CA_HEADER_DATE，因为通过浏览器过来的请求不允许自定义Date（会被浏览器认为是篡改攻击）
        if (headerParams.get(HttpHeaderConstant.HTTP_HEADER_DATE) != null) {
            sb.append(headerParams.get(HttpHeaderConstant.HTTP_HEADER_DATE));
        }
        sb.append(DEFAULT_LN);

        //将headers合成一个字符串
        sb.append(buildHeaders(headerParams));
        sb.append(DEFAULT_LN);

        //将path、queryParam、formParam合成一个字符串
        sb.append(buildResource(pathWithParams, queryParams, formParams));
        return sb.toString();
    }

    /**
     * 将path、queryParam、formParam合成一个字符串
     */
    private static String buildResource(String pathWithParams, Map<String, String> queryParams, Map<String, String> formParams) {
        StringBuilder result = new StringBuilder();
        result.append(pathWithParams);

        //使用TreeMap,默认按照字母排序
        TreeMap<String, String> parameter = Maps.newTreeMap();
        if (queryParams != null && queryParams.size() > 0) {
            parameter.putAll(queryParams);
        }
        if (formParams != null && formParams.size() > 0) {
            parameter.putAll(formParams);
        }

        if (parameter.size() > 0) {
            result.append("?");

            // bugfix by VK.Gao@2017-05-03: "kv separator should be ignored while value is empty, ex. k1=v1&k2&k3=v3&k4"
            List<String> comboMap = new ArrayList<String>();
            for (Entry<String, String> entry : parameter.entrySet()) {
                String comboResult = entry.getKey() + (StringUtils.isNotEmpty(entry.getValue()) ? "=" + entry.getValue() : StringUtils.EMPTY);
                comboMap.add(comboResult);
            }
            Joiner joiner = Joiner.on("&");
            result.append(joiner.join(comboMap));
        }
        return result.toString();
    }

    /**
     * 将headers合成一个字符串
     * 需要注意的是，HTTP头需要按照字母排序加入签名字符串
     * 同时所有加入签名的头的列表，需要用逗号分隔形成一个字符串，加入一个新HTTP头@"X-Ca-Signature-Headers"
     */
    private static String buildHeaders( Map<String, String> headers) {

        if (headers != null && headers.size() > 0) {
            // 筛选出需要签名的key
            Predicate<String> signFilter = input -> input.startsWith(AppSignatureHttpHeaderConstant.X_CA_SIGNATURE_HEADERS);

            // 使用TreeMap,默认按照字母排序
            Map<String, String> headersToSign = new TreeMap<String, String>(Maps.filterKeys(headers, signFilter));

            // 所有加入签名的头的列表，需要用逗号分隔形成一个字符串，加入一个新HTTP头@"X-Ca-Signature-Headers"
            String signHeaders = Joiner.on(',').join(headersToSign.keySet());
            headers.put(AppSignatureHttpHeaderConstant.X_CA_SIGNATURE_HEADERS, signHeaders);

            // 拼装签名内容
            Joiner.MapJoiner joiner = Joiner.on(DEFAULT_LN).withKeyValueSeparator(':');
            return joiner.join(headersToSign);

        } else {
            return StringUtils.EMPTY;
        }


    }

}
