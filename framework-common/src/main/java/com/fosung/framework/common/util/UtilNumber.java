package com.fosung.framework.common.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Longs;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 数字操作类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilNumber extends NumberUtils {

    /**
     * 将字符串转换long类型的数组
     *
     * @param ids
     * @return
     */
    public static Long[] toLongIdArray(String ids) {
        if (UtilString.isBlank(ids)) {
            return null;
        }
        List<String> stringIds = Splitter.on(",").splitToList(ids);

        List<Long> longIds = Lists.newArrayListWithCapacity(stringIds.size());

        for (String stringId : stringIds) {
            if( UtilString.isBlank( stringId ) ){
                continue;
            }
            Long longId = Longs.tryParse(stringId);
            if (longId == null) {
                continue;
            }
            longIds.add(longId);
        }

        return longIds.toArray(new Long[]{});
    }

    /**
     * 将字符串转换long类型的数组
     *
     * @param ids
     * @return
     */
    public static List<Long> toLongIdList(Collection<String> ids) {
        if (UtilCollection.isEmpty(ids)) {
            return null;
        }

        List<Long> longIds = Lists.newArrayListWithCapacity(ids.size());

        for (String stringId : ids) {
            if( UtilString.isBlank( stringId ) ){
                continue;
            }
            Long longId = Longs.tryParse(stringId);
            if (longId == null) {
                continue;
            }
            longIds.add(longId);
        }

        return longIds;
    }

    /**
     * 将字符串转换long类型的数组
     * @param ids
     * @return
     */
    public static Set<Long> toLongIdSet(Collection<String> ids) {
        List<Long> longIds = toLongIdList( ids ) ;

        return UtilCollection.isEmpty( longIds ) ? null : Sets.newHashSet( longIds ) ;
    }

    /**
     * 获取二进制长度标示的最大十进制数
     * @param binaryLength
     * @return
     */
    public static int getMaxNum( int binaryLength ){
        return ( 1 << binaryLength ) - 1 ;
    }

    /**
     * long转换为2进制
     * @param num
     * @return
     */
    public static String toBinary(long num){
        return Long.toString( num , 2 ) ;
    }

    /**
     * int转2进制
     * @param num
     * @return
     */
    public static String toBinary(int num){
        return Integer.toString( num , 2 ) ;
    }

    /**
     * string转换为字符串
     * @param str
     * @return
     */
    public static String toBinary(String str){
        if(StringUtils.isBlank(str)){
            return null ;
        }
        str = str.trim() ;
        Long num = Longs.tryParse(str) ;
        if( num!=null ){
            return toBinary( num ) ;
        }else{
            //将字符串转换为字符数组
            char[] chars = str.toCharArray() ;
            StringBuffer binaryStr = new StringBuffer() ;
            for (char item : chars) {
                binaryStr.append( Integer.toBinaryString( item ) ) ;
            }
            return binaryStr.toString() ;
        }
    }

}
