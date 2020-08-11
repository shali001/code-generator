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
public class FormGeneratorImpl extends BaseGeneratorImpl {

    @Override
    public void initVelocityContext(VelocityContext velocityContext, GeneratorContext generatorContext) {
        super.initVelocityContext(velocityContext, generatorContext);
        String tableName = generatorContext.getTableName();
        Connector connector = (Connector) generatorContext.getAttribute("connector");

        Map<String, String> keyMap = connector.getPrimaryKey(tableName);
        Map<String, String> colMap = connector.mapColumnNameType(tableName);
        Map<String, String> columnRemarkMap = connector.mapColumnRemark(tableName);
        Map<String, String> isNull = connector.mapNull(tableName);
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
        velocityContext.put("methods", generateGetAndSetMethods(colMap, keyMap));
        velocityContext.put("fields", generateFields(colMap, columnRemarkMap, keyMap, isNull,importSets));
        velocityContext.put("importSets", importSets);
        List<String> list = getConvertContent(keySet, keyMap);
        velocityContext.put("converts", list);

    }

    @Override
    public PackageConfigTypes getPackageConfigTypes() {
        if (super.getPackageConfigTypes() == null || super.getPackageConfigTypes().getType() == null) {
            Set<PackageConfigType> packageConfigTypeSet = new HashSet();
            packageConfigTypeSet.add(new PackageConfigType("formPackage", "/dto/form", "Form.java", "form.vm"));
            PackageConfigTypes packageConfigTypes = new PackageConfigTypes();
            packageConfigTypes.setPackageConfigTypeSet(packageConfigTypeSet);
            packageConfigTypes.setType(PackageConfigTypes.ConfigType.FORM);
            setPackageConfigTypes(packageConfigTypes);
        }
        return super.getPackageConfigTypes();
    }

    /**
     * 组织FORM转换实体类
     *
     * @author meng_lbo
     * @date 2020/8/11  14:09
     */
    private List<String> getConvertContent(Set<String> keySet, Map<String, String> keyMap) {
        List<String> list = new ArrayList<>();
        for (String key : keySet) {
            if (keyMap.get("primaryKey").equals(key))
                continue;
            if (key.equals("create_time") || key.equals("update_time") || key.equals("create_user") || key.equals("update_user"))
                continue;
            StringBuilder sb = new StringBuilder();
            sb.append("info.set" + GeneratorStringUtils.firstUpper(key) + "(this." + GeneratorStringUtils.format(key) + ")");
            list.add(sb.toString());
        }
        return list;
    }

    /**
     * 组织属性
     *
     * @author meng_lbo
     * @date 2020/8/11  14:12
     */
    protected List<String> generateFields(Map<String, String> map, Map<String, String> columnRemarkMap, Map<String, String> keyMap, Map<String, String> isNull,Set<String> importSets) {
        Set<String> keySet = map.keySet();
        List<String> fields = new ArrayList();
        for (String key : keySet) {
            if (keyMap.get("primaryKey").equals(key))
                continue;
            if (key.equals("create_time") || key.equals("update_time") || key.equals("create_user") || key.equals("update_user"))
                continue;
            StringBuilder sb = new StringBuilder();
            String value = map.get(key);
            if (!StringUtils.isBlank(columnRemarkMap.get(key))) {
                sb.append("/**").append(LINE);
                sb.append("\t").append(" * ").append(columnRemarkMap.get(key).trim()).append(LINE);
                sb.append("\t").append(" */").append(LINE);
                sb.append("\t");
            }
            if (isNull.get(key).equals("NO")) {
                if (value.equals("String")) {
                    sb.append("@NotBlank(message = \"" + columnRemarkMap.get(key).trim() + "不能为空\")").append(LINE);
                    if (!importSets.contains("NotBlank")) {
                        importSets.add("import javax.validation.constraints.NotBlank;\n");
                    }
                } else {
                    sb.append("@NotNull(message = \"" + columnRemarkMap.get(key).trim() + "不能为空\")").append(LINE);
                    if (!importSets.contains("NotNull")) {
                        importSets.add("import javax.validation.constraints.NotNull;\n");
                    }
                }
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
    protected List<String> generateGetAndSetMethods(Map<String, String> map, Map<String, String> keyMap) {
        Set<String> keySet = map.keySet();
        List<String> methods = new ArrayList();
        for (String key : keySet) {
            if (keyMap.get("primaryKey").equals(key))
                continue;
            if (key.equals("create_time") || key.equals("update_time") || key.equals("create_user") || key.equals("update_user"))
                continue;

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
