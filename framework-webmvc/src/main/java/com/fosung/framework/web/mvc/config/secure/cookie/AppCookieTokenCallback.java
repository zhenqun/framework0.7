package com.fosung.framework.web.mvc.config.secure.cookie;

import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.web.http.AppIBaseController;
import org.apache.commons.lang3.ArrayUtils;
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
@RequestMapping("/api/global/cookie-token.do")
public class AppCookieTokenCallback extends AppIBaseController {

    @Autowired
    private AppProperties appProperties ;

    @RequestMapping(method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> cookieTokenCallback(HttpServletRequest request){

        String cookieToken = "" ;

        Cookie[] cookies = request.getCookies() ;
        if( !ArrayUtils.isEmpty( cookies ) ){
            for (Cookie cookie : cookies) {
                // 只获取指定名称的cookie
                if( ! UtilString.equalsIgnoreCase( appProperties.getSession().getCookieKey() , cookie.getName() )){
                    continue;
                }

                cookieToken = cookie.getValue() ;
            }
        }

        StringBuffer appToken = new StringBuffer(
                "(function(obj){\n" +
                        "        obj.appToken = {\n" +
                        "          token : '"+cookieToken+"'\n" +
                        "        }\n" +
                        "      })(window) ;"
        ) ;

        return ResponseEntity.status(HttpStatus.OK)
                .contentType( MediaType.valueOf("application/javascript") )
                .body( appToken.toString() ) ;

    }


}
