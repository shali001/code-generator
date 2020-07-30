package com.amos.generator.util;


import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;


public class PropertiesUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

    private static Properties properties;
    private static String CONFIG_GENERATOR_PATH = "generator.properties";

    /**
     * 读取 properties
     * @author meng_lbo
     * @date 2020/7/30  16:53
     */
    static {
        String filePath = System.getProperties().getProperty("user.dir");
        List<String> dirAllFiles = listProjectDirAllFiles(filePath);
        if (dirAllFiles == null || dirAllFiles.size() == 0) {
            throw new RuntimeException("读取工程目录下的文件为空.");
        }
        String configFilePath = null;
        for (String dirAllFile : dirAllFiles) {
            if (dirAllFile.endsWith(CONFIG_GENERATOR_PATH)) {
                configFilePath = dirAllFile;
                break;
            }
        }
        PropertiesUtils.properties = new Properties();
        InputStream input = null;
        try {
            LOGGER.info("加载配置文件:" + configFilePath);
            input = new FileInputStream(configFilePath);
            PropertiesUtils.properties.load(input);
        } catch (Exception e) {
            LOGGER.error("加载配置文件出现异常");
            try {
                PropertiesUtils.properties = PropertiesLoaderUtils.loadAllProperties(CONFIG_GENERATOR_PATH);
            } catch (IOException ex) {
                throw new RuntimeException("读取配置文件失败.", e);
            }
        } finally {
            try {
                input.close();
            } catch (IOException var2) {
            }
        }
    }

    public static String getString(String key) {
        String propertiesProperty = PropertiesUtils.properties.getProperty(key);
        if (StrUtil.isNotBlank(propertiesProperty)) {
            propertiesProperty = propertiesProperty.trim();
        }
        return propertiesProperty;
    }

    public static List<String> getList(String key) {
        List<String> list = new ArrayList();
        String arrs = PropertiesUtils.properties.getProperty(key);
        String[] arr = arrs.split(",");
        for (String str : arr) {
            str = str.trim();
            if (!"".equals(str)) {
                list.add(str);
            }
        }
        return list;
    }

    /**
     * 获取所有配置文件
     *
     * @author meng_lbo
     * @date 2020/7/30  16:52
     */
    private static List<String> listProjectDirAllFiles(String projectPath) {
        List<String> fileNames = new ArrayList();
        Vector<String> vector = new Vector<>();
        vector.add(projectPath);
        while (vector.size() > 0) {
            File[] files = new File(vector.get(0)).listFiles();
            vector.remove(0);
            int len = files.length;
            for (int i = 0; i < len; i++) {
                String tmpDir = files[i].getAbsolutePath();
                if (files[i].isDirectory()) {
                    vector.add(tmpDir);
                } else if (tmpDir.endsWith(".properties") && !tmpDir.contains("class")) {
                    fileNames.add(tmpDir);
                }
            }
        }
        return fileNames;
    }


}
