package com.amos.generator.model.impl;

import com.amos.generator.model.BaseGeneratorImpl;
import com.amos.generator.starter.GeneratorContext;
import com.amos.generator.starter.PackageConfigType;
import com.amos.generator.starter.PackageConfigTypes;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 功能描述：Model代码生成
 */
@Component
public class ServiceImplGeneratorImpl extends BaseGeneratorImpl {

    @Override
    public void initVelocityContext(VelocityContext velocityContext, GeneratorContext generatorContext) {
        super.initVelocityContext(velocityContext, generatorContext);
    }

    @Override
    public PackageConfigTypes getPackageConfigTypes() {
        if (super.getPackageConfigTypes() == null || super.getPackageConfigTypes().getType() == null) {
            Set<PackageConfigType> packageConfigTypeSet = new HashSet();
            packageConfigTypeSet.add(new PackageConfigType("serviceImplPackage", "/service/impl", "ServiceImpl.java", "service_impl.vm"));
            PackageConfigTypes packageConfigTypes = new PackageConfigTypes();
            packageConfigTypes.setPackageConfigTypeSet(packageConfigTypeSet);
            packageConfigTypes.setType(PackageConfigTypes.ConfigType.SERVICE_IMPL);
            setPackageConfigTypes(packageConfigTypes);
        }
        return super.getPackageConfigTypes();
    }


}
