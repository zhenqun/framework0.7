package com.fosung.framework.common.util;

import com.fosung.framework.common.exception.AppException;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串的相关操作类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilString extends StringUtils {

    // 包名格式
    public static final Pattern PACKAGE_PATTERN = Pattern.compile("^([a-zA-Z]+[.][a-zA-Z]+)[.]*.*") ;

    // 获取字符串${}中间的内容
    public static final Pattern PARAM$_PATTERN = Pattern.compile("\\$\\{([\\w]*)\\}");

    /**
     * 校验是否是包名格式的字符串
     * @param packageName
     * @return
     */
    public static boolean matcherPackageName(String packageName){
        Assert.hasText(packageName,"包名不能为空");
        Matcher matcher = PACKAGE_PATTERN.matcher(packageName);
        return matcher.find();
    }
    /**
     * 判断是否是手机号码
     *
     * @param phone
     * @return
     */
    public static boolean isValidPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            throw new AppException("手机号应为11位数");
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            Assert.isTrue(isMatch, "手机号码格式不正确");
            return isMatch;
        }
    }


    /**
     * 获取字符串${}中间的内容
     *
     * @param s
     * @return
     */
    public static List<String> getBetween(String s) {
        List<String> results = new ArrayList<String>();
        Matcher m = PARAM$_PATTERN.matcher(s);
        while (m.find()) {
            results.add(m.group(1));
        }
        return results;
    }

    /**
     * 使用逗号连接字符串
     * @param items
     * @return
     */
    public static String joinByComma(Iterable<String> items){
        if( items==null ){
            return "" ;
        }
        return Joiner.on(",").skipNulls().join( items ) ;
    }

    /**
     * 使用逗号分割字符串
     * @param item
     * @return
     */
    public static List<String> splitByComma( String item ){
        if( UtilString.isBlank( item ) ){
            return Lists.newArrayListWithExpectedSize(1) ;
        }

        return Splitter.on(",").omitEmptyStrings().trimResults().splitToList( item ) ;
    }

    /**
     * 格式化请求url，包括：1)去除多余的/
     * @param url
     * @return
     */
    public static String formatUrl(String url){
        url = url.trim().replaceAll("(\\w+)/+","$1/") ;
        return url ;
    }

}
