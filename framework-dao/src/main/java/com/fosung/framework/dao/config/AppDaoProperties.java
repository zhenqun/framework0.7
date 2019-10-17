package com.fosung.framework.dao.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.fosung.framework.common.config.AppProperties;
import com.fosung.framework.common.exception.AppException;
import com.fosung.framework.common.util.UtilString;
import com.fosung.framework.dao.config.jpa.ddl.AppSchemaFilterProvider;
import com.fosung.framework.dao.dialect.MySQLDialectWithoutFK;
import com.fosung.framework.dao.dialect.PostgreSQL9DialectWithoutFK;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dao配置属性类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@ConfigurationProperties(prefix = AppDaoProperties.PREFIX)
@Setter
@Getter
@Slf4j
public class AppDaoProperties {
    /**
     * 配置信息的前缀
     */
    public static final String PREFIX = "app.dao";

    private DataSource datasource = new DataSource();

    private MultipleDataSource dynamic = new MultipleDataSource();

    //mybatis的配置文件
    private Mybatis mybatis = new Mybatis();

    //jpa的配置文件
    private Jpa jpa = new Jpa();

    @Setter
    @Getter
    public static class MultipleDataSource {
        //是否启动多数据源
        private boolean enabled = false;
        //多数据源时默认的主库
        private String masterName = "master";

        private Map<String, DataSource> multiple = new HashMap<>();
    }

    @Setter
    @Getter
    public static class DataSource {

        private boolean enabled = true;

        //数据库连接的名称
        private String name;

        private String driverClass;

        private String url;

        private String username;

        private String password;

        private Pool pool = new Pool();

        public String getDriverClass() {
            if (UtilString.isNotBlank(driverClass)) {
                return driverClass;
            }

            if (UtilString.indexOf(getUrl(), "mysql") != -1) {
                return "com.mysql.jdbc.Driver";
            } else if (UtilString.indexOf(getUrl(), "postgresql") != -1) {
                return "org.postgresql.Driver";
            }

            throw new AppException("请设置数据连接驱动: driverClass");
        }

    }

    @Setter
    @Getter
    public static class Pool {

        private boolean autoCommit = false;

        private int maxActive = 100;

        private int minIdle = 0;

        private String validationQuery = "select 1";

        private int validationQueryTimeout = -1;

        private long timeBetweenEvictionRunsMillis = DruidDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

        /**
         * 最大激活的连接数，必须大于等于最少空闲数
         *
         * @return
         */
        public int getMaxActive() {
            return this.maxActive < this.minIdle ? this.minIdle + 1 : this.maxActive;
        }

    }

    @Setter
    @Getter
    public static class Jpa {

        private boolean enabled = true;

        private String schema = "public" ;

        private List<String> daoPackage = Lists.newArrayList(
                AppProperties.AppPackage.BASE_PACKAGE
        );

        private String dialect;

        private String hbm2ddlFilterProvider = AppSchemaFilterProvider.class.getName();

        private boolean showSql = false;

        private boolean formatSql = true;

        // ddl生成策略，默认执行update
        private String ddlAuto = "update";

        /**
         * 数据库字段值是否可以为空
         */
        private boolean columnIsNullable = true;

        /**
         * 数据库字段值是否唯一，不允许db级别的字段值唯一性校验，在应用中维护
         */
        private boolean columnIsUnique = false;

        /**
         * 不允许db级别的外键映射
         */
        private boolean foreignKeyEnable = false;

        public void setColumnIsNullable(boolean columnIsNullable) {
            this.columnIsNullable = columnIsNullable;
            AppSchemaFilterProvider.COLUMN_NULLABLE = columnIsNullable;
        }

        public void setColumnIsUnique(boolean columnIsUnique) {
            this.columnIsUnique = columnIsUnique;
            AppSchemaFilterProvider.COLUMN_UNIQUE = columnIsUnique;
        }

        public void setForeignKeyEnable(boolean foreignKeyEnable) {
            this.foreignKeyEnable = foreignKeyEnable;
            AppSchemaFilterProvider.FOREIGN_KEY_ENABLE = foreignKeyEnable;
        }

        /**
         * 获取方言类
         *
         * @return
         */
        public String getDialect(AppDaoProperties appDaoProperties) {

            if (UtilString.indexOf(appDaoProperties.getDatasource().getUrl(), "mysql") != -1) {
                return MySQLDialectWithoutFK.class.getName();
            } else if (UtilString.indexOf(appDaoProperties.getDatasource().getUrl(), "postgresql") != -1) {
                return PostgreSQL9DialectWithoutFK.class.getName();
            }

            return UtilString.isNotBlank(dialect) ? dialect : null;
        }
    }

    @Setter
    @Getter
    public static class Mybatis {

        private boolean enabled = true;

        private String config;

        private String dialect = "postgresql";

        private String aliasPackage = Joiner.on(";").join(AppProperties.AppPackage.BASE_PACKAGES);

        private String mapperPackage;

        //加载所有class目录下的mybatis文件
        private List<String> mapperLocations = Lists.newArrayList("classpath*:mapper/*.xml");

        /**
         * 根据数据库连接地址获取dialect
         * @param datasourceURL
         * @return
         */
        public String getDialect(String datasourceURL){
            String dialect = this.getDialect() ;
            if (UtilString.indexOf(datasourceURL, "mysql") != -1) {
                dialect = "mysql" ;
            } else if (UtilString.indexOf(datasourceURL, "postgresql") != -1) {
                dialect = "postgresql" ;
            }

            log.info("mybatis的数据库分页方言为:{}" , dialect) ;

            return dialect ;
        }

    }


}
