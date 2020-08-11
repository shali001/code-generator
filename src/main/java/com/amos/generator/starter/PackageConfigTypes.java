package com.amos.generator.starter;

import java.util.Set;

public class PackageConfigTypes {
    /**
     * 生成文件类型
     */
    private ConfigType type;

    Set<PackageConfigType> packageConfigTypeSet;

    public PackageConfigTypes() {
    }

    public PackageConfigTypes(ConfigType type, Set<PackageConfigType> packageConfigTypeSet) {
        this.type = type;
        this.packageConfigTypeSet = packageConfigTypeSet;
    }

    public ConfigType getType() {
        return type;
    }

    public void setType(ConfigType type) {
        this.type = type;
    }

    public Set<PackageConfigType> getPackageConfigTypeSet() {
        return packageConfigTypeSet;
    }

    public void setPackageConfigTypeSet(Set<PackageConfigType> packageConfigTypeSet) {
        this.packageConfigTypeSet = packageConfigTypeSet;
    }

    public enum ConfigType {
        ENTITY("entity"), DTO("dto"), MAPPER("mapper"), FORM("form");
        public String key;

        ConfigType(String key) {
            this.key = key;
        }
    }
}
