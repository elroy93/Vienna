# vienna
## 描述 
  完全静态的java框架。
* 基于annotationPorcessor的注解处理器实现代码生成，java/class文件。编译器报错。目的是可调试。最好都是java代码。
* 兼容spring的注入形式，包括单类collection注入，map注入，单子类注入。如果一个类有两个实现，但是没有指定注入类型，则会报错。

## TODO 按照优先级
* Inject的字段注入，实现dagger的基本功能。[ ]
  * 注入字段是接口，则注入当前类的实现。如果没有实现、实现没有@Service注释，则报错。[ ]
  * 当前类不是接口，如果当前类有@Service注释，则注入当前类。如果没有，查找子类注入。对于模糊的类型注入，比如当前类和子类都有@Service注解，具体该注入哪一个，需要声明清楚。[ ]
* collection类型的注入，查找当前接口/类的所有带@Service得类进行注入。[ ]
* Guice的module，micronaut的factory，用于对于第三方对象的注入。[ ]
* event类型的注解，实现事件的注入。[ ]
* @Postconstruct注解，实现初始化方法注入。[ ]
* 声明周期的维护，在启动成功和关闭的时候，调用对应的方法，先发送预关闭消息(比如dubbo可以停止接受消息)， 真实关闭需要根据依赖的关系倒置 。[ ]
* AOP实现 [ ]
* graalVM  [ ]

## 配套工具
* idea插件的类型检查。[ ]
* html/uml版本的加载和关闭图。[ ]

## 参考框架列表
* https://docs.micronaut.io/latest/guide/
* https://github.com/google/dagger
* https://github.com/alibaba/fastFFI