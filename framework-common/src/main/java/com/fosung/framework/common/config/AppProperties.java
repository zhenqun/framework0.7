package com.fosung.framework.common.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
/**
 * 系统属性
 * @Author : liupeng
 * @Date : 2019-01-12
 * @Modified By
 */
@ConfigurationProperties(prefix = "app")
@Setter
@Getter
public class AppProperties implements AppConstants{

    // 福生云api的根路径
    private String fosungCloudApiPath = "" ;

    private Session session = new Session();

    private Page page = new Page();

    //当前运行的进程号，实时获取
    private String pid;

    //异常信息配置
    private Exception exception = new Exception() ;

    /**
     * 获取当前应用运行的进程号，根据spring容器配置获取
     *
     * @return
     */
    public String getPid() {
        return System.getProperty("PID");
    }

    @Setter
    @Getter
    public static class Session {

        //session超时时间，单位秒，默认1800秒，即30分钟
        private int timeout = 1800 ;

        //cookie存储session id的key
        private String cookieKey = "_cs" ;

        //cookie所属的domain
        private String cookieDomain ;

        //是否允许在cookie中保存sessionid
        private boolean cookieSessionWritable = true ;

        //是否对cookie的session值进行加密，默认加密
        private boolean cookieSessionEncode = true ;

        //请求header存储session id的key
        private String headerKey = "cs" ;

        //角色编码
        private String roleKey = "roles" ;

        //当前登录用户的id
        private String userIdKey = "userid" ;

    }

    @Setter
    @Getter
    public static class Exception {
        //打印异常栈信息
        private boolean printStackTrace = true ;

    }

    @Setter
    @Getter
    public static class Page {

        //当前分页号，以1开始
        private String pageNum = "pagenum";

        //每页记录最大数
        private String pageSize = "pagesize";

        //每页实际记录数
        private String pageRealSize = "pagerealsize";

        //所有满足条件的记录数
        private String totalElements = "totalelements";

        //所有页数
        private String totalPages = "totalpages";

    }

    /**
     * 框架包目录
     */
    @Setter
    @Getter
    public static class AppPackage {
//        /**
//         * 框架目录
//         */
//        public static final String BASE_PACKAGE_FRAMEWORK = "com.fosung.framework";

        /**
         * 公司代码目录
         */
        public static final String BASE_PACKAGE = "com.fosung";

        public static final String[] BASE_PACKAGES = { BASE_PACKAGE };
    }

    @Setter
    @Getter
    public static class FilterOrder {
        //应用级别的filter，在session加载之前执行
        public static final int APP_FILTER = Ordered.HIGHEST_PRECEDENCE ;

        //session filter，在app的fitler之后执行
        public static final int SESSION_FILTER = Ordered.HIGHEST_PRECEDENCE + 50 ;

        //安全filter，在session filter之后执行
        public static final int SECURE_FILTER = Ordered.HIGHEST_PRECEDENCE + 100 ;
    }


    /**
     * 配置文件执行顺序
     */
    @Setter
    @Getter
    public static class ConfigurationOrder {
        private static final Multimap<Integer, String> ConfigurationOrderMap = HashMultimap.create() ;

//        初始化各个类的执行顺序
        {
            ConfigurationOrderMap.putAll(Ordered.HIGHEST_PRECEDENCE, Lists.newArrayList(
                    "AppLoggingApplicationListener"
            ));
            ConfigurationOrderMap.putAll(Ordered.HIGHEST_PRECEDENCE + 1, Lists.newArrayList(
                    "AppDefaultConfigApplicationListener"
            ));
        }
        /**
         * 获取默认的次序
         *
         * @param simpleClassName
         * @param defaultOrder
         * @return
         */
        public static int getOrder(String simpleClassName, int defaultOrder) {
            int targetOrder = defaultOrder;

            for ( Integer order : ConfigurationOrderMap.keySet() ) {
                if ( ConfigurationOrderMap.get(order).contains(simpleClassName) ) {
                    targetOrder = order;
                    break;
                }
            }

            return targetOrder;
        }
    }

}
