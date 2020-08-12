package com.amos.generator.model.impl;

import com.amos.generator.model.BaseGeneratorImpl;
import com.amos.generator.starter.GeneratorContext;
import com.amos.generator.starter.PackageConfigType;
import com.amos.generator.starter.PackageConfigTypes;
import com.amos.generator.util.PropertiesUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 功能描述：Model代码生成
 */
@Component
public class ServiceGeneratorImpl extends BaseGeneratorImpl {

    @Override
    public void initVelocityContext(VelocityContext velocityContext, GeneratorContext generatorContext) {
        super.initVelocityContext(velocityContext, generatorContext);
        velocityContext.put("author", PropertiesUtils.getString("generator.authorName"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        velocityContext.put("date", dateFormat.format(new Date()));
    }

    @Override
    public PackageConfigTypes getPackageConfigTypes() {
        if (super.getPackageConfigTypes() == null || super.getPackageConfigTypes().getType() == null) {
            Set<PackageConfigType> packageConfigTypeSet = new HashSet();
            packageConfigTypeSet.add(new PackageConfigType("servicePackage", "/service", "Service.java", "service.vm"));
            PackageConfigTypes packageConfigTypes = new PackageConfigTypes();
            packageConfigTypes.setPackageConfigTypeSet(packageConfigTypeSet);
            packageConfigTypes.setType(PackageConfigTypes.ConfigType.SERVICE);
            setPackageConfigTypes(packageConfigTypes);
        }
        return super.getPackageConfigTypes();
    }


}
