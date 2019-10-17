package com.fosung.framework.common.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.primitives.Ints;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ip地址操作类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public interface UtilIp {
    /**
     * 无分类内网域名路由分隔符
     */
    String CIDR_SPLITER  = "/" ;

    /**
     * 是否为有效的ip4地址
     * @param ips
     * @return
     */
    static boolean isValidIp4(String[] ips){

        return Arrays.stream(ips).filter( UtilIp::isValidIp4 ).count() == ips.length ;
    }

    /**
     * 是否为有效的ip4地址
     * @param ip
     * @return
     */
    static boolean isValidIp4(String ip){
        Integer[] ip4 = getIp4Parts( ip ) ;

        return Arrays.stream(ip4).filter( part-> part!=null ).count() == 4 ;
    }

    /**
     * 获取ip4地址的各个部分
     * @param ip
     * @return
     */
    static Integer[] getIp4Parts(String ip){
        Assert.hasText( ip , "ip地址不能为空" );

        if( ip.indexOf( CIDR_SPLITER ) != -1 ){
            ip = ip.substring( 0 , ip.indexOf( CIDR_SPLITER ) ) ;
        }

        //把地址使用"."分割
        List<String> parts = Splitter.on(".").trimResults().omitEmptyStrings().splitToList( ip ) ;

        //将字符串转换为ip4地址格式
        List<Integer> ipParts = parts.stream().map(part->{
            int intPart = Ints.tryParse( part );
            return intPart & 0xff;
        }).collect(Collectors.toList()) ;

        return ipParts.toArray( new Integer[]{} ) ;
    }

    /**
     * 获取ip4地址的byte
     * @param ip
     * @return
     */
    static byte[] getIp4ByteParts(String ip){
        Integer[] ip4 = getIp4Parts( ip ) ;

        byte[] ipByteParts = { ip4[0].byteValue() , ip4[1].byteValue() , ip4[2].byteValue() , ip4[3].byteValue() } ;

        return ipByteParts  ;
    }

    /**
     * 获取ip4地址
     * @param ip
     * @return
     */
    static String getIp4(String ip){
        Integer[] ip4 = getIp4Parts( ip ) ;

        return Joiner.on(".").join( ip4 ) ;
    }

    /**
     * 返回ip地址的cidr
     * @param ip
     * @return
     */
    static int getCIDRPrefix(String ip){
        Assert.hasText( ip , "ip地址不能为空" );

        Integer cidr = null ;

        if( ip.indexOf( CIDR_SPLITER ) != -1 ){
            String cidrStr = ip.substring( ip.indexOf( CIDR_SPLITER ) + 1 ) ;

            if( UtilString.isNotBlank( cidrStr ) ){
                cidr = Ints.tryParse( cidrStr ) ;
            }
        }

        return cidr==null || cidr>32 ? 32 : cidr ;
    }

    /**
     * 格式化地址
     * @param ip
     * @return
     */
    static String formatToIp4CIDR(String ip){
        Integer[] ip4 = getIp4Parts( ip ) ;

        int cidr = getCIDRPrefix( ip ) ;

        return Joiner.on(".").join( ip4 ) + CIDR_SPLITER + cidr ;
    }

    /**
     * 格式化ip地址，将多个ip地址分割为数组格式
     * @param ip
     * @return
     */
    static String[] formatIp4(String ip){
        //将换行符或逗号分隔符替换为换行符
        ip = ip.replaceAll("[(\r\n)(\r)(\n)(,)]+" , "\n") ;

        return Splitter.on("\n").omitEmptyStrings().trimResults()
                .splitToList( ip ).toArray(new String[]{}) ;
    }

    /**
     * 获取ip地址
     * @param inetAddress
     * @return
     */
    static String getIpAddress(InetAddress inetAddress){
        StringBuffer ips = new StringBuffer() ;
        byte[] address = inetAddress.getAddress();
        for (byte addres : address) {
            int net = 0 << 8;
            net |= addres & 0xff ;
            if( ips.length()>0 ){
                ips.append(".") ;
            }
            ips.append( net ) ;
        }
        return ips.toString() ;
    }


}
