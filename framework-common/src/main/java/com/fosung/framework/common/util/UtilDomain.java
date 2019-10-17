package com.fosung.framework.common.util;

import com.google.common.net.InternetDomainName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * 域名操作的util类
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
public class UtilDomain {

    /**
     * 域名匹配的正则表达式
     */
    public static final String DOMAIN_PATTERN = "http[s]?://(.*?)(?::.*?)*/" ;

    /**
     * 由请求的url中抽取请求域名
     * @param url
     * @return
     */
    public static String domainFromURL(String url){
        if( StringUtils.isBlank(url) ){
            return "" ;
        }
        //请求地址必须以“/”结束
        if( !StringUtils.endsWith(url , "/") ){
            url += "/" ;
        }
        Pattern pattern = null ;
        try {
            pattern = new Perl5Compiler().compile(DOMAIN_PATTERN) ;
        }catch (Exception e){
            log.debug("parse domain from url error , url:{} , {}" , url , e.getMessage() );
        }

        PatternMatcher patternMatcher = new Perl5Matcher() ;
        //如果匹配到域名，则返回域名信息，否则返回空字符
        if( patternMatcher.contains(url , pattern) ){
            return patternMatcher.getMatch().group(1) ;
        }

        return "" ;
    }

    /**
     * 是否为顶级域名
     * @param domain
     * @return
     */
    public static boolean isTopDomain(String domain){
        if(StringUtils.isBlank(domain)){
            return false ;
        }
        return InternetDomainName.from(domain.trim()).isTopPrivateDomain() ;
    }

    /**
     * 返回一级域名
     * @param domain
     * @return
     */
    public static String topDomain(String domain){
        if(StringUtils.isBlank(domain)){
            return "" ;
        }
        String topDomain = "" ;
        try{
            topDomain = InternetDomainName.from(domain.trim()).topPrivateDomain().toString() ;
        }catch ( Exception e ){
            //e.printStackTrace() ;
            log.debug("parse top domain error , domain : {}" , domain);
        }
        return topDomain ;
    }

    /**
     * 从url中获取一级域名
     * @param url
     * @return
     */
    public static String topDomainFromURL(String url){
        String domain = domainFromURL(url) ;
        return topDomain( domain ) ;
    }

    /**
     * 返回二级域名
     * @return
     */
    public static String secondDomain(String domain){
        //如果是一级域名，则二级域名只能是空
        if( StringUtils.isBlank(domain) ){
            return "" ;
        }
        String topDomain = topDomain( domain ) ;
        //如果一级域名为空，则不存在二级域名
        if( StringUtils.isBlank(topDomain) ){
            return "" ;
        }

        return StringUtils.removeEnd( StringUtils.removeEnd( domain , topDomain ) , "." ) ;
    }

    /**
     * 从url中获取二级域名
     * @param url
     * @return
     */
    public static String secondDomainFromURL(String url){
        String domain = domainFromURL(url) ;
        return secondDomain( domain ) ;
    }



    public static void main(String[] args){
        String url = "http://test-dasaiztb.glodonedu.com/validcode/img/login" ;

        System.out.println(domainFromURL(url));

//        String domain = "test-per.a.glodonedu.com" ;
//
//        System.out.println("secondDomain="+secondDomain(domain).replaceAll("[-.]" , "_")) ;
//
//        System.out.println("topDomain="+topDomain(domain)) ;

//        domain = "127.0.0.1:8080" ;
//
//        System.out.println("secondDomain="+secondDomain(domain)) ;
//
//        System.out.println("topDomain="+topDomain(domain)) ;

//        domain = "test-per.b.glodonedu.com" ;
//
//        System.out.println("secondDomain="+secondDomain(domain)) ;
//
//        System.out.println("topDomain="+topDomain(domain)) ;
//
    }

}
