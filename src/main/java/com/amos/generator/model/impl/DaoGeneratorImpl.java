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
public class DaoGeneratorImpl extends BaseGeneratorImpl {

    @Override
    public void initVelocityContext(VelocityContext velocityContext, GeneratorContext generatorContext) {
        super.initVelocityContext(velocityContext, generatorContext);
    }

    @Override
    public PackageConfigTypes getPackageConfigTypes() {
        if (super.getPackageConfigTypes() == null || super.getPackageConfigTypes().getType() == null) {
            Set<PackageConfigType> packageConfigTypeSet = new HashSet();
            packageConfigTypeSet.add(new PackageConfigType("daoPackage", "/dao", "Dao.java", "dao.vm"));
            PackageConfigTypes packageConfigTypes = new PackageConfigTypes();
            packageConfigTypes.setPackageConfigTypeSet(packageConfigTypeSet);
            packageConfigTypes.setType(PackageConfigTypes.ConfigType.DAO);
            setPackageConfigTypes(packageConfigTypes);
        }
        return super.getPackageConfigTypes();
    }


}
