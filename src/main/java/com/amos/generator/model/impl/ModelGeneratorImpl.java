package com.amos.generator.model.impl;

import com.amos.generator.connect.Connector;
import com.amos.generator.model.BaseGeneratorImpl;
import com.amos.generator.starter.GeneratorContext;
import com.amos.generator.starter.PackageConfigType;
import com.amos.generator.starter.PackageConfigTypes;
import com.amos.generator.util.GeneratorFileUtils;
import com.amos.generator.util.GeneratorStringUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 功能描述：Model代码生成
 */
@Component
public class ModelGeneratorImpl extends BaseGeneratorImpl {

    @Override
    public void initVelocityContext(VelocityContext velocityContext, GeneratorContext generatorContext) {
        super.initVelocityContext(velocityContext, generatorContext);
        velocityContext.put("SerialVersionUID", String.valueOf(UUID.randomUUID().getLeastSignificantBits()));

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
        velocityContext.put("fields", generateFields(colMap, columnRemarkMap,keyMap));
        velocityContext.put("importSets", importSets);
    }

    @Override
    public PackageConfigTypes getPackageConfigTypes() {
        if(super.getPackageConfigTypes()==null||super.getPackageConfigTypes().getType()==null){
            Set<PackageConfigType> packageConfigTypeSet = new HashSet();
            packageConfigTypeSet.add(new PackageConfigType("poPackage", "/entity", ".java", "entity.vm"));
            packageConfigTypeSet.add(new PackageConfigType("modelPackage", "/dto", "Bean.java", "dto.vm"));
            PackageConfigTypes packageConfigTypes=new PackageConfigTypes();
            packageConfigTypes.setPackageConfigTypeSet(packageConfigTypeSet);
            packageConfigTypes.setType(PackageConfigTypes.ConfigType.MODEL);
            setPackageConfigTypes(packageConfigTypes);
        }
        return super.getPackageConfigTypes();
    }
}
