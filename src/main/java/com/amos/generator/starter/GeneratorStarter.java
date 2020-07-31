package com.amos.generator.starter;

import com.amos.generator.model.Generator;

import java.util.Set;

/**
 * 功能描述：自动化生成代码接口
 */
public interface GeneratorStarter {

    /**
     * 启动创建代码
     */
    void start(Set<Generator> generatorSet);
}
