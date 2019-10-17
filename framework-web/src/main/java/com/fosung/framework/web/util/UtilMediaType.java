package com.fosung.framework.web.util;

import com.fosung.framework.common.util.UtilString;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.Set;

/**
 * http请求的内容类型
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilMediaType {

    private static Map<MediaType,Set<String>> mediaTypes = Maps.newHashMap() ;

    public static final MediaType DEFAULT_MEDIA_TYPE  = MediaType.APPLICATION_OCTET_STREAM ;

    static {
        Set<String> imageTypes = Sets.newHashSet( "png", "jpg", "jpeg", "gif", "bmp" ) ;
        mediaTypes.put( MediaType.IMAGE_JPEG , imageTypes ) ;
    }

    /**
     * 根据文件后缀获取 MediaType
     * @param extension
     * @return
     */
    public static MediaType parseMediaType(String extension){
        if(UtilString.isBlank( extension )){
            return DEFAULT_MEDIA_TYPE ;
        }

        // 裁剪扩展名，并转换为小写
        extension = UtilString.removeStart( extension.trim() , "." ).toLowerCase() ;

        MediaType mediaType = DEFAULT_MEDIA_TYPE ;
        for (Map.Entry<MediaType, Set<String>> entry : mediaTypes.entrySet()) {
            if( entry.getValue().contains( extension ) ){
                mediaType = entry.getKey() ;
                break;
            }
        }

        return mediaType ;
    }


}
