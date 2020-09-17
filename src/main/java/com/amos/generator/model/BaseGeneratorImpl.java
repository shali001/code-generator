package com.amos.generator.model;

import com.amos.generator.starter.GeneratorContext;
import com.amos.generator.starter.PackageConfigType;
import com.amos.generator.starter.PackageConfigTypes;
import com.amos.generator.util.FileUtil;
import com.amos.generator.util.GeneratorFileUtils;
import com.amos.generator.util.GeneratorStringUtils;
import com.amos.generator.util.PropertiesUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能描述：读取配置文件，生成代码基本实现类
 */
public class BaseGeneratorImpl implements Generator {

    protected static String LINE = System.getProperty("line.separator");
    /**
     * velocity上下文
     */
    protected VelocityContext velocityContext;

    /**
     * 模板存放文件夹
     */
    public static String VM_TARGET_PATH = "template";

    /**
     * 模板存放文件后缀
     */
    public static String VM_TARGET_EXT = ".vm";

    /**
     * 获取生成目录类型
     */
    private PackageConfigTypes packageConfigTypes;

    public String getVmTargetPath() {
        return VM_TARGET_PATH;
    }

    public void setVmTargetPath(String vmTargetPath) {
        VM_TARGET_PATH = vmTargetPath;
    }

    public String getVmTargetExt() {
        return VM_TARGET_EXT;
    }

    public void setVmTargetExt(String vmTargetExt) {
        VM_TARGET_EXT = vmTargetExt;
    }

    public PackageConfigTypes getPackageConfigTypes() {
        return packageConfigTypes;
    }

    public void setPackageConfigTypes(PackageConfigTypes packageConfigTypes) {
        this.packageConfigTypes = packageConfigTypes;
    }

    /**
     * 自动化创建业务代码
     *
     * @param context
     */
    public void defaultGenerator(GeneratorContext context, Map<String, String> allPackageNameMap) throws Exception {
        velocityContext = new VelocityContext();
        Properties properties = initDefaultProperties();
        VelocityEngine velocityEngine = new VelocityEngine(properties);
        velocityEngine.init();
        write(context, allPackageNameMap, velocityEngine);
    }

    /**
     * 插件读取模板文件要从jar包中读取
     *
     * @param context
     */
    public void pluginGenerator(GeneratorContext context, Map<String, String> allPackageNameMap) throws Exception {
        velocityContext = new VelocityContext();
        Properties properties = initPluginProperties();
        VelocityEngine velocityEngine = new VelocityEngine(properties);
        velocityEngine.init();
        write(context, allPackageNameMap, velocityEngine);
    }

    /**
     * 读取配置渲染模板，生成文件
     *
     * @param generatorContext
     */
    protected void write(GeneratorContext generatorContext, Map<String, String> allPackageNameMap, VelocityEngine velocityEngine) {
        // 读取模板渲染内容，同时创建文件
        Map<String, String> params = initGeneratorParams(generatorContext, allPackageNameMap);
        for (String templateName : params.keySet()) {
            try {
                Template template = velocityEngine.getTemplate(VM_TARGET_PATH + "/" + templateName, "UTF-8");
                initVelocityContext(velocityContext, generatorContext);
                StringWriter writer = new StringWriter();
                template.merge(velocityContext, writer);
                String content = writer.toString();
                GeneratorFileUtils.write(content, params.get(templateName));
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化Velocity配置
     */
    protected Properties initDefaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute");
        properties.setProperty(Velocity.RESOURCE_LOADER, "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        return properties;
    }

    /**
     * 初始化Velocity配置
     */
    protected Properties initPluginProperties() {
        Properties properties = new Properties();
        properties.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute");
        //设置velocity资源加载方式为jar
        properties.setProperty("resource.loader", "jar");
        properties.setProperty("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
        properties.setProperty("jar.resource.loader.path", "jar:" + getVmFilePath4Plugin());
        properties.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        return properties;
    }

    /**
     * 检查模版文件是否存在
     *
     * @param fileDir
     */
    private boolean checkTemplateExists(String fileDir) {
        boolean flag = false;
        File templateFile = new File(fileDir);
        if (templateFile.exists()) {
            FileFilter fileFilter = new FileFilter() {
                public boolean accept(File file) {
                    return file.getName().endsWith(VM_TARGET_EXT);
                }
            };
            List<File> fileList = FileUtil.getAllFilePath(fileDir, fileFilter);
            if (fileList != null && fileList.size() > 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 初始化上下文
     *
     * @param velocityContext
     * @param generatorContext
     * @return
     */
    public void initVelocityContext(VelocityContext velocityContext, GeneratorContext generatorContext) {
        velocityContext.put("basePackageName_base", PropertiesUtils.getString("generator.basePackage.base"));
        velocityContext.put("tableName", generatorContext.getTableName());
        velocityContext.put("upClassName", generatorContext.getUpClassName());
        velocityContext.put("lowClassName", generatorContext.getLowClassName());
        velocityContext.put("basePackageName", PropertiesUtils.getString("generator.basePackage"));
        velocityContext.put("primaryKeyType", generatorContext.getPrimaryKeyType());
        velocityContext.put("primaryKey", generatorContext.getPrimaryKey());
        velocityContext.put("normalPrimaryKey", generatorContext.getAttribute("normalPrimaryKey"));
        velocityContext.put("typeAliases", generatorContext.getAttribute("typeAliases"));
        velocityContext.put("mappers", generatorContext.getAttribute("mappers"));
        Map<String, String> packageNamesMap = generatorContext.getAllPackageNamesMap();
        if (packageNamesMap != null && packageNamesMap.size() > 0) {
            for (String packageNameKey : packageNamesMap.keySet()) {
                velocityContext.put(packageNameKey, packageNamesMap.get(packageNameKey));
            }
        }
        StringBuilder titleSb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String authorName = StringUtils.isBlank(generatorContext.getAuthorName()) ? System.getProperty("user.name") : generatorContext.getAuthorName();
        titleSb.append("/** ").append(LINE)
                .append(" * ").append("<br/>").append(LINE)
                .append(" * Created by ").append(authorName).append(" on ").append(dateFormat.format(new Date())).append(LINE)
                .append(" */");
        velocityContext.put("classTitle", titleSb.toString());

    }

    /**
     * 初始化生成文件的模板及其文件名称
     *
     * @param context
     */
    protected Map<String, String> initGeneratorParams(GeneratorContext context, Map<String, String> allPackageNameMap) {

        Set<PackageConfigType> packageConfigTypeSet = this.getPackageConfigTypes().getPackageConfigTypeSet();

        Properties properties = context.getProperties();
        String tableName = context.getTableName();

        Map<String, String> generatorParams = new HashMap();
        for (PackageConfigType packageConfigType : packageConfigTypeSet) {
            String targetDir = packageConfigType.getTargetDir();
            String fileNameSuffix = packageConfigType.getFileNameSuffix();
            String template = packageConfigType.getTemplate();
            String fileName;
            context.getAllPackageNamesMap().putAll(allPackageNameMap);
            fileName = GeneratorFileUtils.getPackageDirectory(targetDir)
                    + GeneratorStringUtils.firstUpperAndNoPrefix(tableName)
                    + fileNameSuffix;
            generatorParams.put(template, fileName);
        }
        return generatorParams;
    }


    /**
     * 获取目录
     */
    protected String getVmFilePath() {
        String fileDir = null;
        try {
            fileDir = Thread.currentThread().getContextClassLoader().getResource(VM_TARGET_PATH).getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileDir;
    }

    protected String getVmFilePath4Plugin() {
        ClassLoader clToUse = ClassUtils.getDefaultClassLoader();
        try {
            Enumeration<URL> urls = clToUse.getResources(VM_TARGET_PATH);
            URL url = urls.nextElement();
            String filePath = url.getFile();
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("read velocity templates error, e", e);
        }
    }


}
