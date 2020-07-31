package com.amos.generator;

import com.amos.generator.model.Generator;
import com.amos.generator.starter.GeneratorStarter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

@SpringBootTest
class CodeGeneratorApplicationTests {
    @Autowired
    private Set<Generator> set;
    @Autowired
    private GeneratorStarter generatorStarter;

    @Test
    void contextLoads() {
        //System.out.println(set);
    	generatorStarter.start(set);
    }

}
