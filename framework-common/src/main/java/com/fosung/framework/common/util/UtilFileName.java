package com.fosung.framework.common.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件名操作类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilFileName extends FilenameUtils {

    /**
     * 获取文件扩展名，支持网络url参数的方式
     * @param filename
     * @return
     */
    public static String getExtension(final String filename) {
        String extension = FilenameUtils.getExtension( filename ) ;
        if(StringUtils.indexOf(extension , "?")!=-1){
            extension = extension.substring( 0 , extension.indexOf("?")) ;
        }

        return StringUtils.isBlank(extension) ? "" : extension.trim() ;

    }

}
