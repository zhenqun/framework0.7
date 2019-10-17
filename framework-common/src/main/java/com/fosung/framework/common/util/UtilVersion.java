package com.fosung.framework.common.util;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

/**
 * 版本号管理操作类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public abstract class UtilVersion {
    /**
     * 获取二进制长度表示的最大十进制数
     * @param binaryLength
     * @return
     */
    public int getMaxNum( int binaryLength ){
        return ( 1 << binaryLength ) - 1 ;
    }

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        //对版本进行校验
        if(StringUtils.isBlank(version1) && StringUtils.isNotBlank(version2)){
            return -1 ;
        }else if (StringUtils.isNotBlank(version1) && StringUtils.isBlank(version2)){
            return 1 ;
        }else if(StringUtils.isBlank(version1) && StringUtils.isBlank(version2)){
            return 0 ;
        }

        //对版本号进行分隔
        String[] versionArray1 = Splitter.on(".").trimResults().splitToList(version1).toArray(new String[]{}) ;
        String[] versionArray2 = Splitter.on(".").trimResults().splitToList(version2).toArray(new String[]{}) ;

        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

}
