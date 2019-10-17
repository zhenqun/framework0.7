package com.fosung.framework.common.secure.dataccess;

/**
 * 用户信息以及请求是否需要过滤
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public class APIDataAccessPrincipalHolder {

    private static ThreadLocal<APIDataAccessPrincipal> apiDataAccessPrincipalThreadLocal = new ThreadLocal<APIDataAccessPrincipal>();

//    // 下一次查询是否使用数据权限
//    private static ThreadLocal<Boolean> nextAccessAuthQueryDisabled = new ThreadLocal<>() ;

    /**
     * 设置线程用户信息
     *
     * @param APIDataAccessPrincipal
     */
    public static void setPrincipalHolder(APIDataAccessPrincipal APIDataAccessPrincipal) {
        apiDataAccessPrincipalThreadLocal.set(APIDataAccessPrincipal);
//        nextAccessAuthQueryDisabled.set( false ) ;
    }

    /**
     * 获取线程的用户信息
     *
     * @return
     */
    public static APIDataAccessPrincipal getPrincipalHolder() {
        APIDataAccessPrincipal APIDataAccessPrincipal = apiDataAccessPrincipalThreadLocal.get();
        return APIDataAccessPrincipal;
    }

//     /**
//     * 禁用下一次认证参数查询
//     * @return
//     */
//     public static void disableNextAccessAuthQueryOnce(){
//         nextAccessAuthQueryDisabled.set( true ) ;
//     }

//    /**
//     * 启用下一次认证参数查询
//     * @return
//     */
//    public static void enableNextAccessAuthQueryOnce(){
//        nextAccessAuthQueryDisabled.set( false ) ;
//    }
//
//    /**
//     * 是否禁用数据权限校验
//     * @return
//     */
//    public static boolean isAccessAuthQueryDisabled(){
//        nextAccessAuthQueryDisabled.set( false ) ;
//        return nextAccessAuthQueryDisabled.get()!=null && nextAccessAuthQueryDisabled.get() ;
//    }

    /**
     * 清除线程的用户信息
     */
    public static void clearAPIDataAccessPrincipalHolder() {
        apiDataAccessPrincipalThreadLocal.remove() ;
//        nextAccessAuthQueryDisabled.remove() ;
    }
}
