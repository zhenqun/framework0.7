package com.fosung.framework.web.util;

import com.google.common.base.Strings;
import com.google.common.primitives.Longs;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.lang3.StringUtils;


/**
 * @author toquery
 * @version 1
 */
public class UtilWebNettySign {

    public static String getRequestHeaderValue(DefaultHttpRequest request, String requestHeaderKey) {
        return getRequestHeaderValue(request, requestHeaderKey, null);
    }

    public static String getRequestHeaderValue(DefaultHttpRequest request, String requestHeaderKey, String defaultValue) {
        HttpHeaders httpHeaders = request.headers();
        String value = httpHeaders.get(requestHeaderKey);
        return Strings.isNullOrEmpty(value) ? defaultValue : value;
    }

    public static Long getRequestHeaderTimestamp(DefaultHttpRequest request, String requestHeaderKey) {
        HttpHeaders httpHeaders = request.headers();
        String value = httpHeaders.get(requestHeaderKey);
        Long sTime = StringUtils.isNotBlank(value) ? Longs.tryParse(value) : null;
        return sTime == null ? 0 : sTime;
    }



}
