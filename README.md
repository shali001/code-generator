# code-generator
springboot代码生成器

入口方法为test包下：
          `CodeGeneratorApplicationTests.contextLoads()`;  
所有的参数配置在`generator.properties`文件中配置
该代码生成器根据实际项目需要编写，生成了部分常用接口，因此也增加了部分自定义的异常处理，返回结果处理,基础包等代码
如果不需要，在生成的代码中删除即可，不影响整体功能
```$xslt
import com.hongen.admin.enums.ResultEnums;
import com.hongen.base.bean.Result;
import com.hongen.base.tool.ResultUtil;
//以上3个类为返回结果处理类

import com.hongen.base.exception.BusinessException;
import com.hongen.base.exception.controller.ControllerExceptionHandler;
import com.hongen.base.exception.service.ServiceExceptionHandler;
//以上3个类为自定义的异常处理类

import com.hongen.base.service.BaseService;
import com.hongen.base.dao.BaseDao;
//tk.mybatis基本方法封装
```
所有引入（import）的类后续会在amos_base项目中上传，根据需要自行取用。

该项目背景是使用tk.mybatis,因此 mapper.xml中没有生成常用sql

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
