# code-generator
springboot代码生成器

入口方法为test包下：
          `CodeGeneratorApplicationTests.contextLoads()`;  
所有的参数配置在`generator.properties`文件中配置

```#mysql连接配置
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://127.0.0.1:3306/card_service
jdbc.username=amos
jdbc.password=@Mysql213!
generator.authorName=lingbo.meng
#是否生成注释
generator.annotation=true
#生成代码位置
generator.location=src
#文件包名称
generator.project.name=demo
#生成那些层
generator.layers=mapper,mapperConfig,model,service,result
#包名称
generator.basePackage=com.hongen.college
#表名称，多个用逗号分隔(,)
generator.tables=t_store
#过滤掉代码表的前缀
generator.table.prefix=t_
#浮点型转化为：BigDecimal，否则转化为：Double
generator.precision=high
```
