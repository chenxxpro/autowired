# Autowired 

Autowired 是使用Java8编写的基于类成员变量的注入类库，它不依赖于任何容器，可以独立使用。当然少不了**Simple&Powerful&Lightweight**关键字。

## What

Autowired并不是一个反射注入类库，在运行时，不会产生任何反射副作用。它可以说是一个Bean自动构建与加载管理工具类；
Autowired的使用场景是常驻内存的服务性质的类注入管理，例如DB服务、MyBatis服务、Redis等与程序生命周期一致的服务注入。

它提供以下特性：

1. 与Spring.@Autowired注解类似的功能：使用配置文件来声明Bean，并在需要的地方自动加载；
1. 与Spring.@Autowired不同的是,Autowired并非IOC反射注入,而是LazyLoad懒加载；
1. 当然,默认情况下是Lazy,也可配置为初始化时预加载； 

## Why

话说每个轮子总有一个Why。简单的说是：

> 我昨天堵在深圳南坪快速路上前后左右被都大货车包围着脑子跑偏了思考怎么解决公司项目在没有使用Spring吃货框架支持的IOC情况下
> 实现某些服务类组件自动注入的问题并且不能引入太多重型类库框架并且能够使用我深爱的final关键字的情况下回到家就开始编写的轮子

## How

### Step 0

添加依赖仓库

Gradle
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/yoojia/maven" 
    }
}
```

Maven
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<settings xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd'
          xmlns='http://maven.apache.org/SETTINGS/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
    <profiles>
        <profile>
            <repositories>
                <repository>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <id>bintray-yoojia-maven</id>
                    <name>bintray</name>
                    <url>https://dl.bintray.com/yoojia/maven</url>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <id>bintray-yoojia-maven</id>
                    <name>bintray-plugins</name>
                    <url>https://dl.bintray.com/yoojia/maven</url>
                </pluginRepository>
            </pluginRepositories>
            <id>bintray</id>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>bintray</activeProfile>
    </activeProfiles>
</settings>
```

引入依赖：

```groovy
compile 'net.nextabc:autowired:1.1.0'
```

Maven
```xml
<dependency>
  <groupId>net.nextabc</groupId>
  <artifactId>autowired</artifactId>
  <version>1.1.0</version>
  <type>pom</type>
</dependency>
```

### Step 1

在 `src/main/resources` 或者其它Classpath目录下，添加`autowired.xml`配置文件：

```xml
<?xml version="1.0" encoding="utf-8" ?>
<autowired>

    <!--自动注入Bean列表-->
    <bean id="YOUR_id"
          class="java.io.File"
          factory="net.nextabc.demo.FileFactory"
          preload="true"
    >
        <init-params>
            <param key="path">/home/yoojia/JavaProjects/Autowired/build.gradle</param>
        </init-params>
    </bean>
</autowired>
```

配置文件中声明需要自动注入的Bean配置。

### Step 2

```java
public class Test1 {

    private static final Autowired<File> myAutowired = Autowired.id("YOUR_id");

    public void test() {
        // 使用myAutowired成员变量的get方法，可以获取 “YOUR_id” 配置的Bean对象。
        File f = myAutowired.get();
        System.out.println(f.getAbsolutePath());
    }
}
```

如上代码，创建一个Autowired成员变量，修饰可以static也可以final，调用Autowired.getBean()函数获取对应的Bean对象。这个对象是由BeanFactory创建的。

Bean对象一旦被创建，会留驻内存，重复调用同一个id的Bean,其Bean对象只会被创建一次,之后的调用将直接使用缓存对象。

## GetBean

Autowired提供了两个创建方法函数:

1. `Autowired.id(String)` 明确指定id,创建与xml配置bean.id一致的Bean绑定；
1. `Autowired.type(Class)` 以Class.name作为id；

Autowired在调用getBean来获取Bean对象时,内部基于`beanId`来获取.
 
通常情况,每个Class类型创建一个Bean对象,并建议使用id属性作为beanId. Autowired优先使用为xml配置的bean.id属性；如果没有设置,则使用class类型名称作为beanId.

但是需要注意,无论使用id,还是class名称作为beanId,他们都必须保持唯一性,否则Autowired在初始化时会报错的.

## 配置参数说明

#### Bean的属性说明

- `id` Bean的id；它与类成员变量声明时`Autowired.id(String)`指定的id是一一对应的；
- `class` Bean的类名称；它与类成员变量声明时`Autowired.type(Class)`指定的类名是一一对应的；
- `factofy` 创建Bean的Factory工厂类。class与factory两项配置至少指定一项目；优先使用factory创建Bean对象；
- `preload` 默认情况为false，即每个Bean都是Lazy Load,即当第一次调用时才会创建，并留驻内存；如果为true则在Autowired初始化时自动创建Bean对象.

#### class/factory的说明

`class`和`factory`都是创建Bean对象的关键类，两个参数不能同时为空。

这两个参数的逻辑是这样的：

重点:
1. class/factory同时设置时，factory会接收到class的非空Class<?>对象和initArgs参数来创建；

扩展:
1. 只有factory，未设置class时: 使用factory创建，但其中Class<?>参数对象为空。这意味着factory对于自己要创建的Bean是全知的；
1. 只有class，未设置factory时: 使用默认DefaultBeanFactory来创建。DefaultBeanFactory创建对象的逻辑另有说明；

#### DefaultBeanFactory

默认创建Bean工厂类，可以创建简单类型的Bean对象，它内部创建Bean对象策略如下：

1. 如果配置指定的参数为空，直接使用newInstance创建；
2. 如果存在无参数构造函数，直接使用newInstance创建；
3. 如果存在只有一个Map类型的构造函数，直接将Map参数传入构造器；
4. 如果配置指定的参数只有一个，尝试基本类型的构造器并解析参数类型来创建；
5. 如果配置指定的参数有多个，暂不支持；

#### autowired.xml的文件位置

按以下顺序搜索`autowired.xml`文件:

1. `System.getProperty("autowired.configPath")` 指定的文件地址；
1. `System.getProperty("user.dir")` 目录下的配置文件；
1. `classpath:autowired.xml` Classpath路径下的配置文件；

## Q&A

**Q: 如果要创建相同class类型,不同参数的Bean怎么办?**

>A: 使用不同的Id来区分即可；

**Q: DefaultBeanFactory为什么不支持多个参数呢？**

> A: 多个参数的类构造函数，已经很复杂，这种情况下花费大量自动化逻辑来自动构造是不划算的。