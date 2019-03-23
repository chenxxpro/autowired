# Autowired 

Autowired 是使用Java8编写的基于类成员变量的注入类库，它不依赖于任何容器，可以独立使用。当然少不了**Simple&Powerful&Lightweight**关键字。

## What

Autowired并不是一个反射注入类库，在运行时，不会产生任何反射副作用。它可以说是一个Bean自动构建与加载管理工具类；
Autowired的使用场景是常驻内存的服务性质的类注入管理，例如DB服务、MyBatis服务、Redis等与程序生命周期一致的服务注入。

它提供以下特性：

1. 与Sprint.@Autowired 注解类似的功能：使用配置文件来声明Bean，并在需要的地方自动加载；
1. 默认Lazy懒加载；可配置预加载； 

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
compile 'net.nextabc:autowired:1.0.0'
```

Maven
```xml
<dependency>
  <groupId>net.nextabc</groupId>
  <artifactId>autowired</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

### Step 1

在 `src/main/resources` 或者其它Classpath目录下，添加`autowired.xml`配置文件：

```xml
<?xml version="1.0" encoding="utf-8" ?>
<autowired>

    <!--自动注入Bean列表-->
    <bean id="YOUR_IDENTIFY"
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

    private static final Autowired myAutowired = Autowired.identify("YOUR_IDENTIFY");

    public void test() {
        // 使用myAutowired成员变量的get方法，可以获取 “YOUR_IDENTIFY” 配置的Bean对象。
        File f = myAutowired.get();
        System.out.println(f.getAbsolutePath());
    }
}
```

如上代码，创建一个Autowired成员变量，可以static/final，调用Autowired.get()函数获取对应的Bean对象。这个对象是由BeanFactory创建的。

Bean对象一旦被创建，会留驻内存，重复调用同一个identify的Bean只会被创建一次。

## 配置参数说明

#### Bean的属性说明

- `id` Bean的Identify。它与类成员变量声明时指定的Identify是一一对应的；
- `class` Bean的类名称；
- `factofy` 创建Bean的Factory工厂类。
- `preload` 默认情况下，每个Bean都是Lazy Load；即当第一次调用时才会创建，并留驻内存；

#### class/factory的说明

`class`和`factory`都是创建Bean对象的关键类，两个参数不能同时为空。这两个参数的逻辑是这样的：

1. class/factory同时设置时，factory会接收到class的非空Class<?>对象和initArgs参数来创建；
1. 只有factory，未设置class时，可以使用factory创建，其中Class<?>对象为空。意味着factory对于自己要创建的Bean是已知的；
1. 只有class，未设置factory时，使用默认DefaultBeanFactory来创建。

#### DefaultBeanFactory

默认BeanFactory工厂类，可以创建简单类型的Bean对象，它内部创建Bean对象策略如下：

1. 如果参数为空，直接使用newInstance创建；
3. 如果参数只有一个，并且是Map类型，直接将Map参数传入构造器；
2. 如果参数只有一个，尝试String/int/Integer/long/Long/float/Float/double/Double/boolean/Boolean等基本类型的构造器并解析参数来创建；
4. 如果参数有多个，暂不支持；

Q: **为什么不支持多个参数呢？**

> A: 多个参数的类构造函数，已经很复杂，这种情况下花费大量自动化逻辑来自动构造是不划算的。