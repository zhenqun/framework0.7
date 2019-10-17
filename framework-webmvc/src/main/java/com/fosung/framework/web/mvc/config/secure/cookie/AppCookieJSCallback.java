package com.fosung.framework.web.mvc.config.secure.cookie;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.http.AppIBaseController;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 跨域cookie共享，将共享cookie信息
 * @Author : liupeng
 * @Date : 2019-03-15
 * @Modified By
 */
@RequestMapping("/api/global/cookie-js.do")
public class AppCookieJSCallback extends AppIBaseController {

    /**
     * jsonp cookie回写的有效时间
     */
    private static final int JSONP_COOKIE_KEY_EXPIRES = 365 ;

    @Autowired
    private AppProperties appProperties ;

    @RequestMapping(method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> cookieCallback(HttpServletRequest request){

        StringBuffer jsonpCookie = new StringBuffer(
                "(function(){function _sc(cn,v,ed){var ct=new Date();ct.setDate(ct.getDate()+ed);document.cookie=[" +
                        "cn , '=' , v , " +
                        "';expires=' , ct.toGMTString() , " +
                        "';path=/'" +
                        "].join('') ; console.log('========='+v);}"
        ) ;

        Cookie[] cookies = request.getCookies() ;
        if( !ArrayUtils.isEmpty( cookies ) ){
            for (Cookie cookie : cookies) {
                // 只获取指定名称的cookie
                if( ! UtilString.equalsIgnoreCase( appProperties.getSession().getCookieKey() , cookie.getName() )){
                    continue;
                }

                jsonpCookie.append( "_sc('"+appProperties.getSession().getCookieKey()+ new DateTime().toString("MM_ss") +"','"+cookie.getValue()+"',"+JSONP_COOKIE_KEY_EXPIRES+");" ) ;
            }
        }

        jsonpCookie.append( "})() ;" ) ;

        return ResponseEntity.status(HttpStatus.OK)
                .contentType( MediaType.valueOf("application/javascript") )
                .body( jsonpCookie.toString() ) ;

    }


}
