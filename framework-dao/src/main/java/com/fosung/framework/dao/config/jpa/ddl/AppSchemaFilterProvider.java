package com.fosung.framework.dao.config.jpa.ddl;

import org.hibernate.tool.schema.internal.DefaultSchemaFilterProvider;
import org.hibernate.tool.schema.spi.SchemaFilter;

/**
 * 对ddl schema的生成策略，定制化
 * @Author : liupeng
 * @Date : 2018/7/29 18:30
 * @Modified By
 */
public class AppSchemaFilterProvider extends DefaultSchemaFilterProvider {

    /**
     * 列允许为空限制
     */
    public static boolean COLUMN_NULLABLE  = true ;

    /**
     * 列值唯一性限制
     */
    public static boolean COLUMN_UNIQUE = false ;

    /**
     * 外键是否允许创建
     */
    public static boolean FOREIGN_KEY_ENABLE = false ;

    @Override
    public SchemaFilter getCreateFilter() {
        return new AppSchemaFilter() ;
    }

    @Override
    public SchemaFilter getMigrateFilter() {
        return new AppSchemaFilter() ;
    }

}
