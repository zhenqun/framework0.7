package com.fosung.framework.web.util;

import com.google.common.base.Strings;
import com.google.common.primitives.Longs;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author toquery
 * @version 1
 */
public class UtilWebSign {

    public static String getRequestHeaderValue(HttpServletRequest request, String requestHeaderKey) {
        return getRequestHeaderValue(request, requestHeaderKey, null);
    }

    public static String getRequestHeaderValue(HttpServletRequest request, String requestHeaderKey, String defaultValue) {
        String value = request.getHeader(requestHeaderKey);
        return Strings.isNullOrEmpty(value) ? defaultValue : value;
    }

    public static Long getRequestHeaderTimestamp(HttpServletRequest request, String requestHeaderKey) {
        String value = request.getHeader(requestHeaderKey);
        Long sTime = StringUtils.isNotBlank(value) ? Longs.tryParse(value) : null;
        return sTime == null ? 0 : sTime;
    }


}
