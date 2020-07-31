package com.amos.generator.util;

import cn.hutool.core.util.StrUtil;
import com.amos.generator.starter.PackageConfigType;
import com.amos.generator.starter.PackageConfigTypes;

import java.io.*;
import java.util.*;

public class GeneratorFileUtils {

    public static boolean write(String content, String path) {
        try {
            OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
            BufferedWriter writer = new BufferedWriter(writerStream);
            writer.write(content);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void createPackageDirectory(Set<PackageConfigTypes> packageConfigTypesSet) {

        String location = PropertiesUtils.getString("generator.location");
        String packageLayer = PropertiesUtils.getString("generator.layers");
        String project = PropertiesUtils.getString("generator.project.name");
        if (StrUtil.isNotBlank(project)) {
            location = location + "/" + PropertiesUtils.getString("generator.project.name");
        }
        String packageDir = "/" + PropertiesUtils.getString("generator.basePackage").replaceAll("[.]", "/");
        String[] packageLayerArr = packageLayer.split(",");
        Set<String> typeSet = new HashSet<>();
        typeSet.addAll(Arrays.asList(packageLayerArr));
        if (StrUtil.isNotBlank(project)) {
            for (PackageConfigTypes packageConfigTypes : packageConfigTypesSet) {
                for (PackageConfigType packageConfigType : packageConfigTypes.getPackageConfigTypeSet()) {
                    String targetDir = packageConfigType.getTargetDir();
                    mkDir(location + packageDir + targetDir);
                }
            }
        }
    }

    private static void mkDir(String dir) {
        File file;
        file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String getPackageDirectory(String name) {
        String location = PropertiesUtils.getString("generator.location");
        String packageDir = "/" + PropertiesUtils.getString("generator.basePackage").replaceAll("[.]", "/");
        String project = PropertiesUtils.getString("generator.project.name");
        String directory;
        if (StrUtil.isNotBlank(project)) {
            directory = location + "/" + PropertiesUtils.getString("generator.project.name") + packageDir + "/" + name + "/";
        } else {
            directory = location + packageDir + "/" + name + "/";
        }
        return directory;
    }

    public static Map<String, String> getAllPackageName(Set<PackageConfigTypes> packageConfigTypesSet) {
        String basePackage = PropertiesUtils.getString("generator.basePackage");
        Map<String, String> packageNameMap = new HashMap<>();
        for (PackageConfigTypes packageConfigTypes : packageConfigTypesSet) {
            for (PackageConfigType packageConfigType : packageConfigTypes.getPackageConfigTypeSet()) {
                String targetDir = packageConfigType.getTargetDir();
                StringBuilder packageNameStrb = new StringBuilder();
                packageNameStrb.append(basePackage);
                String packageNameValue = targetDir.replaceAll("/", ".");
                if (packageNameValue.startsWith(".")) {
                    packageNameStrb.append(packageNameValue);
                } else {
                    packageNameStrb.append(".").append(packageNameValue);
                }
                packageNameMap.put(packageConfigType.getAliasType(), packageNameStrb.toString());
            }
        }
        return packageNameMap;
    }

    public static Map<String, String> getAllTypeAliasesMap(String tableName, Set<PackageConfigTypes> packageConfigTypesSet) {
        Map<String, String> typeAliasesMap = new TreeMap<>();
        for (PackageConfigTypes packageConfigTypes : packageConfigTypesSet) {
            if (PackageConfigTypes.ConfigType.MODEL.equals(packageConfigTypes.getType())) {
                for (PackageConfigType packageConfigType : packageConfigTypes.getPackageConfigTypeSet()) {
                    String fileNameSuffix = packageConfigType.getFileNameSuffix();
                    if (fileNameSuffix.endsWith(".java")) {
                        String targetDir = packageConfigType.getTargetDir();
                        targetDir = targetDir.replaceAll("/", ".");
                        String basePackage = PropertiesUtils.getString("generator.basePackage");
                        String tempFileNameSuffix = fileNameSuffix.replace("{domainSuffix}", PropertiesUtils.getString("generator.domain.suffix"));
                        tempFileNameSuffix = tempFileNameSuffix.replace("{queryDomainSuffix}", PropertiesUtils.getString("generator.queryDomain.suffix"));
                        tempFileNameSuffix = tempFileNameSuffix.replaceAll("(\\.java)", "");
                        String className = basePackage + targetDir + "." + GeneratorStringUtils.firstUpperAndNoPrefix(tableName) + tempFileNameSuffix;
                        String alias = className.substring(className.lastIndexOf(".") + 1);
                        typeAliasesMap.put(alias, className);
                    }
                }
            }
        }
        return typeAliasesMap;
    }

    public static Map<String, String> getAllMappersMap(String tableName, Set<PackageConfigTypes> packageConfigTypesSet) {
        Map<String, String> mappersMap = new TreeMap<>();
        for (PackageConfigTypes packageConfigTypes : packageConfigTypesSet) {
            if (PackageConfigTypes.ConfigType.MAPPER.equals(packageConfigTypes.getType())) {
                for (PackageConfigType packageConfigType : packageConfigTypes.getPackageConfigTypeSet()) {
                    String fileNameSuffix = packageConfigType.getFileNameSuffix();
                    if (fileNameSuffix.endsWith(".xml")) {
                        String targetDir = packageConfigType.getTargetDir();
                        String basePackage = PropertiesUtils.getString("generator.basePackage").replaceAll("[.]", "/");
                        String fileName = basePackage + targetDir + "/"
                                + GeneratorStringUtils.firstUpperAndNoPrefix(tableName)
                                + fileNameSuffix;
                        mappersMap.put(GeneratorStringUtils.firstUpperAndNoPrefix(tableName), fileName);
                    }
                }
            }
        }
        return mappersMap;
    }
}