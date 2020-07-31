package com.amos.generator.model;

import com.amos.generator.starter.GeneratorContext;
import com.amos.generator.starter.PackageConfigTypes;

import java.util.Map;

/**
 * 功能描述：读取配置自动化生成代码接口
 */
public interface Generator {
    /**
     * 模块的包配置类型
     */
    PackageConfigTypes getPackageConfigTypes();

    /**
     * 读取配置生成文件
     *
     * @param context
     */
    void defaultGenerator(GeneratorContext context, Map<String, String> allPackageNameMap) throws Exception;


}
