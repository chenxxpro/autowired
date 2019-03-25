package net.nextabc.autowired;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
class XMLConfig {

    private static final Logger LOGGER = Logger.getLogger(XMLConfig.class);

    private final Element docElement;

    XMLConfig(String config) {
        try (final InputStream is = Clazz.CLASS_LOADER.getResourceAsStream(config)) {
            if (is == null) {
                throw new IllegalStateException("Resource not found: " + config);
            }
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(is);
            docElement = doc.getDocumentElement();
            docElement.normalize();
        } catch (Exception e) {
            LOGGER.error("invalid config: " + config);
            throw new RuntimeException("Failed to parse config: " + config);
        }
    }

    List<BeanConfig> getBeans() {
        final List<BeanConfig> output = new ArrayList<>();
        final String defaultBeanFactoryClass = DefaultBeanFactory.class.getName();
        getElementList(docElement, "bean").forEach(beanEle -> {
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
            getElementList(beanEle, "init-params").forEach(initParamEle -> {
                getElementList(initParamEle, "param").forEach(param -> {
                    initParams.put(param.getAttribute("key"), param.getTextContent());
                });
            });

            final String preload = beanEle.getAttribute("preload");
            // 确保非空
            // 默认BeanFactory
            output.add(new BeanConfig(
                    identify,
                    beanClass,
                    initParams,
                    factoryClass,
                    "true".equalsIgnoreCase(preload)));
        });
        return output;
    }


    private List<Element> getElementList(Element parent, String tag) {
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
