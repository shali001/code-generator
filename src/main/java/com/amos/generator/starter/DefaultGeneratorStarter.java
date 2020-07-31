package com.amos.generator.starter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.amos.generator.connect.Connector;
import com.amos.generator.connect.MysqlConnector;
import com.amos.generator.model.Generator;
import com.amos.generator.util.GeneratorFileUtils;
import com.amos.generator.util.GeneratorStringUtils;
import com.amos.generator.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 功能描述：自动化生成代码默认执行实现
 */
@Component
public class DefaultGeneratorStarter implements GeneratorStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGeneratorStarter.class);

    /**
     * 读取数据连接
     */
    private static Connector connector;

    static {

        //generatorConfigurer.initConfigParams();
        connector = new MysqlConnector();
    }

    /**
     * 执行生成方法
     */
    public void start(Set<Generator> generatorSet) {
        try {
            generator(generatorSet);
        } catch (Exception e) {
            throw new RuntimeException("启动创建代码工具出现异常:" + e.getMessage(), e);
        }
    }

    /**
     * 自动化创建代码文件
     */
    protected void generator(Set<Generator> generatorSet) {
        LOGGER.info("*********************代码生成工具,开始自动生成代码*********************");

        List<String> tableNames = PropertiesUtils.getList("generator.tables");
        if (CollectionUtil.isEmpty(tableNames)) {
            String tablePrefix = PropertiesUtils.getString("generator.table.prefix");
            if (StrUtil.isNotBlank(tablePrefix)) {
                List<String> tableList = connector.listTablesByTablePrefix(tablePrefix);
                if (CollectionUtil.isEmpty(tableList)) {
                    throw new RuntimeException("无法获取配置代码中表前缀的数据表，请检查配置配置表前缀.");
                }
                tableNames.addAll(tableList);
            }
        }
        if (CollectionUtils.isEmpty(tableNames)) {
            throw new RuntimeException("配置代码表配置或者表前缀配置无法获取数据库表，请检查表配置和表前缀配置.");
        }

        Set<PackageConfigTypes> packageConfigTypesSet4PKN = new HashSet<>();
        for (Generator generator : generatorSet) {
            packageConfigTypesSet4PKN.add(generator.getPackageConfigTypes());
        }
        Map<String, String> allPackageNameMap = GeneratorFileUtils.getAllPackageName(packageConfigTypesSet4PKN);
        for (Map.Entry<String, String> entry : allPackageNameMap.entrySet()) {
            LOGGER.info("*********************Packages[packageNameKey:" + entry.getKey() + ",packageNameValue:" + entry.getValue() + "]");
        }

        //自动化生成文件
        Map<String, String> allTypeAliasesMap = new TreeMap<>();
        Map<String, String> allMappersMap = new TreeMap<>();
        for (String tableName : tableNames) {
            LOGGER.info("*********************代码生成工具,开始自动生成代码>>>" + tableName);
            try {
                Map<String, String> pkMap = connector.getPrimaryKey(tableName);
                if (StrUtil.isBlank(pkMap.get("primaryKey"))) {
                    throw new RuntimeException(tableName + " 表结构没有主键,请检查表结构,生成代码失败.");
                }
            } catch (Exception e) {
                throw e;
            }
            //要生成的模块分层
            String layerConfig = PropertiesUtils.getString("generator.layers");
            String[] layers = StrUtil.split(layerConfig, ",");
            if (ArrayUtil.isEmpty(layers)) {
                throw new RuntimeException(tableName + " 读取配置文件分层结构为空,请检查配置是否按照逗号隔开.");
            }
            Set<String> typeSet = new HashSet<>();//使用set去重
            typeSet.addAll(Arrays.asList(layers));
            LOGGER.info("*********************代码生成工具,开始自动生成代码>>>" + layerConfig);
            Set<PackageConfigTypes> packageConfigTypesSet = new HashSet<>();
            for (Generator generator : generatorSet) {
                if (typeSet.contains(generator.getPackageConfigTypes().getType().key)) {
                    packageConfigTypesSet.add(generator.getPackageConfigTypes());
                }
            }
            GeneratorFileUtils.createPackageDirectory(packageConfigTypesSet); // 创建目录

            Map<String, String> typeAliasesMap = GeneratorFileUtils.getAllTypeAliasesMap(tableName, packageConfigTypesSet4PKN);
            for (Map.Entry<String, String> entry : typeAliasesMap.entrySet()) {
                LOGGER.info("*********************MapperConfigTypeAlias[key:" + entry.getKey() + ",type:" + entry.getValue() + "]");
            }
            Map<String, String> mappersMap = GeneratorFileUtils.getAllMappersMap(tableName, packageConfigTypesSet4PKN);
            for (Map.Entry<String, String> entry : mappersMap.entrySet()) {
                LOGGER.info("*********************MapperConfigMappers[key:" + entry.getKey() + ",resource:" + entry.getValue() + "]");
            }
            allTypeAliasesMap.putAll(typeAliasesMap);
            allMappersMap.putAll(mappersMap);

            for (Generator generator : generatorSet) {
                if (packageConfigTypesSet.contains(generator.getPackageConfigTypes())) {
                    try {
                        GeneratorContext generatorContext = initBaseContext(tableName, allTypeAliasesMap, allMappersMap);
                        doGeneratorService(generator, generatorContext, allPackageNameMap);
                    } catch (Exception e) {
                        LOGGER.error("Can not Generate tableName:" + tableName + ",configTypes:" + generator.getPackageConfigTypes().getType(), e);
                    }
                }
            }
            LOGGER.info("*********************代码生成工具,已经结束生成代码>>>" + tableName);
        }
        LOGGER.info("*********************代码生成工具,已经结束生成代码*********************");
    }

    /**
     * 调用创建模板的方式
     *
     * @param generator
     * @param generatorContext
     * @param allPackageNameMap
     */
    protected void doGeneratorService(Generator generator, GeneratorContext generatorContext, Map<String, String> allPackageNameMap) {
        try {
            generator.defaultGenerator(generatorContext, allPackageNameMap);
        } catch (Exception e) {
            LOGGER.error("模板生成异常", e);
        }
    }

    /**
     * 初始化渲染模板基本参数上下文
     *
     * @param tableName
     */
    protected GeneratorContext initBaseContext(String tableName, Map<String, String> allTypeAliasesMap, Map<String, String> allMappersMap) {
        Map<String, String> propMap = connector.getPrimaryKey(tableName);
        String authorName = PropertiesUtils.getString("generator.authorName");
        String upClassName = GeneratorStringUtils.firstUpperAndNoPrefix(tableName);
        String lowClassName = GeneratorStringUtils.formatAndNoPrefix(tableName);
        String packageName = PropertiesUtils.getString("generator.basePackage");
        String primaryKeyType = propMap.get("primaryKeyType");
        String primaryKey = GeneratorStringUtils.firstUpperNoFormat(GeneratorStringUtils.format(propMap.get("primaryKey")));
        String columnPrimaryKey = propMap.get("primaryKey");
        String normalPrimaryKey = GeneratorStringUtils.format(propMap.get("primaryKey"));

        GeneratorContext generatorContext = new GeneratorContext(authorName, tableName, upClassName, lowClassName,
                packageName, primaryKeyType, primaryKey, PropertiesUtils.getProperties());
        generatorContext.addAttribute("connector", connector);
        generatorContext.addAttribute("properties", PropertiesUtils.getProperties());
        generatorContext.addAttribute("columnPrimaryKey", columnPrimaryKey);
        generatorContext.addAttribute("normalPrimaryKey", normalPrimaryKey);
        generatorContext.addAttribute("domainSuffix", PropertiesUtils.getString("generator.domain.suffix"));
        generatorContext.addAttribute("queryDomainSuffix", PropertiesUtils.getString("generator.queryDomain.suffix"));
        generatorContext.addAttribute("typeAliases", allTypeAliasesMap);
        generatorContext.addAttribute("mappers", allMappersMap);
        return generatorContext;
    }
}
