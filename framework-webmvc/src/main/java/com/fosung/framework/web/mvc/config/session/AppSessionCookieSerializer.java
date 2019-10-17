package com.fosung.framework.web.mvc.config.session;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.exception.AppException;
import com.fosung.framework.common.util.UtilCollection;
import com.fosung.framework.common.util.UtilDigest;
import com.fosung.framework.web.http.ResponseParam;
import com.fosung.framework.web.util.UtilWeb;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.session.web.http.DefaultCookieSerializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AppSessionCookieSerializer extends DefaultCookieSerializer {

    private AppProperties appProperties ;

    public AppSessionCookieSerializer(AppProperties appProperties){
        this.appProperties = appProperties ;
    }

    @Override
    public List<String> readCookieValues(HttpServletRequest request) {
        List<String> cookieValues = super.readCookieValues(request) ;

        try{
            cookieValues = decodeValue( request , cookieValues ) ;
        }catch( Exception e ){
            log.error( "请求( {} )中cookie解码失败" , request.getRequestURI() , e ) ;
            if( request.getAttribute( AppSessionAutoConfiguration.SESSION_RESPONSE ) != null ){
                HttpServletResponse response = (HttpServletResponse)request.getAttribute( AppSessionAutoConfiguration.SESSION_RESPONSE ) ;
                //返回数据给前端
                UtilWeb.writeToResponse( response , ResponseParam.fail()
                        .message("you may be a bad person , sorry~~").getResponseEntity(HttpStatus.BAD_REQUEST) ) ;
            }
            //抛出异常
            throw e ;
        }

        return cookieValues ;
    }

   @Override
    public void writeCookieValue(CookieValue cookieValue) {
        //只有允许在cookie中写sessionid时，才进行写操作
        if( appProperties.getSession().isCookieSessionWritable() ){
            CookieValue writeCookieValue = encodeValue( cookieValue ) ;
            log.debug("回写请求 {} 的cookie值:{}" ,
                    writeCookieValue.getRequest()!=null ? writeCookieValue.getRequest().getRequestURI():"" ,
                    writeCookieValue.getCookieValue() ) ;
            super.writeCookieValue( writeCookieValue ) ;
        }else{
            log.info("不允许将sessionid( {} )存储到cookie中" , cookieValue.getCookieValue());
        }
    }

    /**
     * 对cookie值进行解密
     * @param request
     * @param values
     * @return
     */
    public List<String> decodeValue( HttpServletRequest request , List<String> values ){
        if( !appProperties.getSession().isCookieSessionEncode() || UtilCollection.isEmpty( values )){
            return values ;
        }

        String salt = getCookieValueSalt( request ) ;

        //验证cookie写的值是否正确
        long validValueNum = values.stream().filter(value->{
            int spliter = value.lastIndexOf("_") ;
            if( spliter<0 ){
                return false ;
            }
            //获取请求中带的salt
            String requestSalt = value.substring( spliter + 1 ) ;

            return requestSalt.equalsIgnoreCase( salt ) ;
        }).count() ;

        //如果正常解码的数量 与 请求中的数量不符，则解码失败并抛出异常
        if( validValueNum != values.size() ){
            throw new AppException("cookie值解码错误") ;
        }

        //只返回存储的sessionid
        return values.stream().map(value->{
            int spliter = value.lastIndexOf("_") ;

            return value.substring( 0 , spliter ) ;
        }).collect( Collectors.toList() ) ;
    }

    /**
     * 对cookie值进行加密
     * @param cookieValue
     * @return
     */
    public CookieValue encodeValue( CookieValue cookieValue ){
        if( !appProperties.getSession().isCookieSessionEncode() ){
            return cookieValue ;
        }

        String salt = getCookieValueSalt( cookieValue.getRequest() ) ;

        //重新构造一个新的cookievalue
        String newValue = cookieValue.getCookieValue() + "_" + salt ;

        return new CookieValue( cookieValue.getRequest() , cookieValue.getResponse() , newValue ) ;
    }

    /**
     * 获取与请求相关的请求信息
     * @param request
     * @return
     */
    public String getCookieValueSalt( HttpServletRequest request ){
        StringBuffer salt = new StringBuffer() ;

        List<String> headerNames = Lists.newArrayList( HttpHeaders.USER_AGENT ) ;

        headerNames.stream().forEach( headerName->{
            salt.append( request.getHeader( headerName ) ) ;
        } );

        return UtilDigest.encodeMD5( salt.toString() ) ;
    }

}
