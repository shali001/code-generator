package com.amos.generator.connect;

import java.util.List;
import java.util.Map;

/**
 * 数据库连接
 */
public interface Connector {

    /**
     * 获取表的键值类型
     *
     * @param tableName
     */
    Map<String, String> getPrimaryKey(String tableName);

    /**
     * 获取字段类型
     *
     * @param tableName
     */
    Map<String, String> mapColumnNameType(String tableName);

    /**
     * 获取字段备注
     *
     * @param tableName
     */
    Map<String, String> mapColumnRemark(String tableName);

    /**
     * 获取是否可以为空
     *
     * @param tableName
     */
    Map<String, String> mapNull(String tableName);

    /**
     * 获取所有的索引信息
     *
     * @param tableName
     */
    List<String> listAllIndex(String tableName);

    List<String> listTablesByTablePrefix(String tablePrefix);
}
