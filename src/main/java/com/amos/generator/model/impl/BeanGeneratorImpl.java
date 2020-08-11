package com.amos.generator.model.impl;

import com.amos.generator.connect.Connector;
import com.amos.generator.model.BaseGeneratorImpl;
import com.amos.generator.starter.GeneratorContext;
import com.amos.generator.starter.PackageConfigType;
import com.amos.generator.starter.PackageConfigTypes;
import com.amos.generator.util.GeneratorStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 功能描述：Model代码生成
 */
@Component
public class BeanGeneratorImpl extends BaseGeneratorImpl {

    @Override
    public void initVelocityContext(VelocityContext velocityContext, GeneratorContext generatorContext) {
        super.initVelocityContext(velocityContext, generatorContext);
        String tableName = generatorContext.getTableName();
        Connector connector = (Connector) generatorContext.getAttribute("connector");

        Map<String, String> keyMap = connector.getPrimaryKey(tableName);
        Map<String, String> colMap = connector.mapColumnNameType(tableName);
        Map<String, String> columnRemarkMap = connector.mapColumnRemark(tableName);
        Set<String> keySet = colMap.keySet();
        Set<String> importSets = new HashSet<>();
        for (String key : keySet) {
            String value = colMap.get(key);
            if ("BigDecimal".equals(value) && !importSets.contains("BigDecimal")) {
                importSets.add("import java.math.BigDecimal;\n");
            } else if ("Date".equals(value) && !importSets.contains("Date")) {
                importSets.add("import java.util.Date;\n");
            } else if ("Timestamp".equals(value) && !importSets.contains("Timestamp")) {
                importSets.add("import java.sql.Timestamp;\n");
            }
        }
        velocityContext.put("methods", generateGetAndSetMethods(colMap));
        velocityContext.put("fields", generateFields(colMap, columnRemarkMap));
        velocityContext.put("importSets", importSets);
        List<String> list = getConvertContent(keySet);
        velocityContext.put("do2dto", list);

    }

    @Override
    public PackageConfigTypes getPackageConfigTypes() {
        if (super.getPackageConfigTypes() == null || super.getPackageConfigTypes().getType() == null) {
            Set<PackageConfigType> packageConfigTypeSet = new HashSet();
            packageConfigTypeSet.add(new PackageConfigType("dtoPackage", "/dto", "Bean.java", "dto.vm"));
            PackageConfigTypes packageConfigTypes = new PackageConfigTypes();
            packageConfigTypes.setPackageConfigTypeSet(packageConfigTypeSet);
            packageConfigTypes.setType(PackageConfigTypes.ConfigType.DTO);
            setPackageConfigTypes(packageConfigTypes);
        }
        return super.getPackageConfigTypes();
    }

    /**
     * 组织实体类属性转换为bean属性
     *
     * @author meng_lbo
     * @date 2020/8/11  14:09
     */
    private List<String> getConvertContent(Set<String> keySet) {
        List<String> list = new ArrayList<>();
        for (String key : keySet) {
            StringBuilder sb = new StringBuilder();
            sb.append("this." + GeneratorStringUtils.format(key) + " = info.get" + GeneratorStringUtils.firstUpper(key) + "()");
            list.add(sb.toString());
        }
        return list;
    }

    /**
     * 组织属性
     * @author meng_lbo
     * @date 2020/8/11  14:12
     */
    protected List<String> generateFields(Map<String, String> map, Map<String, String> columnRemarkMap) {
        Set<String> keySet = map.keySet();
        List<String> fields = new ArrayList();
        for (String key : keySet) {
            StringBuilder sb = new StringBuilder();
            String value = map.get(key);
            if (!StringUtils.isBlank(columnRemarkMap.get(key))) {
                sb.append("/**").append(LINE);
                sb.append("\t").append(" * ").append(columnRemarkMap.get(key).trim()).append(LINE);
                sb.append("\t").append(" */").append(LINE);
                sb.append("\t");
            }
            sb.append("private ").append(value + " ").append(GeneratorStringUtils.format(key) + ";").append(LINE);
            fields.add(sb.toString());
        }
        return fields;
    }

    /**
     * 组织get set方法
     *
     * @author meng_lbo
     * @date 2020/8/11  14:11
     */
    protected List<String> generateGetAndSetMethods(Map<String, String> map) {
        Set<String> keySet = map.keySet();
        List<String> methods = new ArrayList();
        for (String key : keySet) {
            StringBuilder getSb = new StringBuilder();
            StringBuilder setSb = new StringBuilder();
            String field = GeneratorStringUtils.format(key);
            String fieldType = map.get(key);
            //generate get method
            getSb.append("public ").append(fieldType + " ").append("get" + GeneratorStringUtils.firstUpperNoFormat(field) + "() {").append(LINE).append("\t\t")
                    .append("return " + field + ";").append(LINE).append("\t}");
            //generate set method
            setSb.append("public ").append("void ").append("set" + GeneratorStringUtils.firstUpperNoFormat(field) + "(" + fieldType + " " + field + ") {").append(LINE).append("\t\t")
                    .append("this." + field + " = " + field + ";").append(LINE).append("\t}");
            methods.add(getSb.toString());
            methods.add(setSb.toString());
        }
        return methods;
    }


}
