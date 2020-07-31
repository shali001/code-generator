package com.amos.generator.starter;

import cn.hutool.core.util.StrUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Velocity渲染模板配置上下文信息
 */
public class GeneratorContext implements Serializable {
    private static final long serialVersionUID = -8244453504134436716L;
    /**
     * 配置文件配置
     */
    private Properties properties;
    /**
     * 作者
     */
    private String authorName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 类的名称
     */
    private String upClassName;

    /**
     * 按照JAVA规范，类对应的变量小写
     */
    private String lowClassName;

    /**
     * 基础包名
     */
    private String basePackageName;

    /**
     * 所有包名
     */
    private Map<String, String> allPackageNamesMap = new HashMap<>();

    /**
     * 主键类型
     */
    private String primaryKeyType;

    /**
     * 主键
     */
    private String primaryKey;

    /**
     * 上下文参数
     */
    private Map<String, Object> attributes = new HashMap();

    public GeneratorContext(String authorName, String tableName, String upClassName, String lowClassName, String basePackageName,
                            String primaryKeyType, String primaryKey, Properties properties) {
        this.authorName = authorName;
        this.tableName = tableName;
        this.upClassName = upClassName;
        this.lowClassName = lowClassName;
        this.basePackageName = basePackageName;
        this.primaryKeyType = primaryKeyType;
        this.primaryKey = primaryKey;
        this.properties = properties;
    }

    public Object getAttribute(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        return this.attributes.get(key);
    }

    public void addAttribute(String key, Object value) {
        if (StrUtil.isBlank(key)) {
            return;
        }
        this.attributes.put(key, value);
    }

    public Properties getProperties() {
        return properties;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getUpClassName() {
        return upClassName;
    }

    public String getLowClassName() {
        return lowClassName;
    }

    public String getBasePackageName() {
        return basePackageName;
    }

    public String getPrimaryKeyType() {
        return primaryKeyType;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public Map<String, String> getAllPackageNamesMap() {
        return allPackageNamesMap;
    }
}
