package net.nextabc.autowired;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

/**
 * XML配置文件工具类
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
class XmlConfig {

    private static final String XML_CONFIG_NAME = "autowired.xml";

    private final Element docElement;

    XmlConfig() {
        // 加载配置文件的路径顺序:
        // 1. System.getenv("Autowired.config")
        // 2. System.getProperty("user.dir")/autowired.xml
        // 3. Classpath.resource/autowired.xml
        final Resource res = Resource.from(
                Resource.File(getenv("Autowired.config")),
                Resource.File(Paths.get(getProperty("user.dir"), XML_CONFIG_NAME).toFile()),
                Resource.Classpath(XML_CONFIG_NAME)
        );
        try (final InputStream is = res.getOrNull()) {
            if (is == null) {
                throw new IllegalStateException("Resource not found: " + XML_CONFIG_NAME);
            }
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(is);
            docElement = doc.getDocumentElement();
            docElement.normalize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse config: " + XML_CONFIG_NAME);
        }
    }

    /**
     * 获取Bean配置列表
     *
     * @return List
     */
    List<XmlBeanConfig> getXmlBeanConfigs() {
        final List<XmlBeanConfig> output = new ArrayList<>();
        final String defaultBeanFactoryClass = DefaultBeanFactory.class.getName();
        getXmlElementList(docElement, "bean").forEach(beanEle -> {
            final String identify = beanEle.getAttribute("id");

            String factoryClass = beanEle.getAttribute("factory");
            final String beanClass = beanEle.getAttribute("class");
            // Bean.class / Bean.factory 不能同时为空
            final boolean hasBean = !isEmpty(beanClass);
            final boolean hasFactory = !isEmpty(factoryClass);
            if (!hasBean && !hasFactory) {
                throw new IllegalArgumentException("Bean.class or Bean.factory must be specified");
            }
            // 只有Bean.class，并且未设置Bean.Factory，则使用默认Factory
            if (hasBean && !hasFactory) {
                factoryClass = defaultBeanFactoryClass;
            }
            // 初始化参数
            final Map<String, String> initParams = new HashMap<>(4);
            getXmlElementList(beanEle, "init-params").forEach(initParamEle -> {
                getXmlElementList(initParamEle, "param").forEach(param -> {
                    final String key = param.getAttribute("key");
                    String value = param.getTextContent();
                    // 优先读取环境变量的值,来替换配置文件的值
                    final String envKey = param.getAttribute("envKey");
                    if (envKey != null && !envKey.isEmpty()) {
                        final String envValue = System.getenv(envKey);
                        if (envValue != null) {
                            value = envValue;
                        }
                    }
                    initParams.put(key, value);
                });
            });

            final String preload = beanEle.getAttribute("preload");
            // 确保非空
            // 默认BeanFactory
            output.add(new XmlBeanConfig(
                    identify,
                    beanClass,
                    initParams,
                    factoryClass,
                    "true".equalsIgnoreCase(preload)));
        });
        return output;
    }

    private List<Element> getXmlElementList(Element parent, String tag) {
        final NodeList nodes = parent.getElementsByTagName(tag);
        final List<Element> list = new ArrayList<>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                list.add((Element) node);
            }
        }
        return list;
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
