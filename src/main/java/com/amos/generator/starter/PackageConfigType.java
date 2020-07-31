package com.amos.generator.starter;


import cn.hutool.core.util.StrUtil;

public class PackageConfigType {
    /**
     * 别名
     */
    private String aliasType;
    /**
     * 生成目标文件夹
     */
    private String targetDir;

    /**
     * 生成文件后缀
     */
    private String fileNameSuffix = ".java";

    /**
     * 生成文件模板
     */
    private String template;

    public PackageConfigType() {
    }

    public PackageConfigType(String aliasType, String targetDir, String fileNameSuffix, String template) {
        this.aliasType = aliasType;
        this.targetDir = targetDir;
        this.fileNameSuffix = fileNameSuffix;
        this.template = template;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public String getFileNameSuffix() {
        return fileNameSuffix;
    }

    public void setFileNameSuffix(String fileNameSuffix) {
        this.fileNameSuffix = StrUtil.isNotBlank(fileNameSuffix) ? fileNameSuffix : this.fileNameSuffix;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getAliasType() {
        return aliasType;
    }

    public void setAliasType(String aliasType) {
        this.aliasType = aliasType;
    }
}
