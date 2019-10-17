package com.fosung.framework.common.util;

/**
 * 运行时操作系统类型识别
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilOS {

    /**
     * 为windows系统
     * @return
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name") ;

        return os.toLowerCase().startsWith("win") ;
    }

    /**
     * 为linux系统
     * @return
     */
    public static boolean isLinux() {
        String os = System.getProperty("os.name") ;

        return os.toLowerCase().startsWith("linux") ;
    }

    /**
     * 为mac系统
     * @return
     */
    public static boolean isMac() {
        String os = System.getProperty("os.name") ;

        return os.toLowerCase().contains("mac") ;
    }

}
