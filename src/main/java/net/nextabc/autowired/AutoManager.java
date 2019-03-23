package net.nextabc.autowired;


import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
class AutoManager {

    private static final String XML_CONFIG = "autowired.xml";

    private static final Logger LOGGER = Logger.getLogger(AutoManager.class);

    private static final Map<String, BeanField> IDENTIFY_REGISTER = new HashMap<>();

    private AutoManager() {
    }

    static void init() {
        final XMLConfig xml = new XMLConfig(XML_CONFIG);
        // 从配置文件中加载
        final Map<String, BeanFactory> factories = new HashMap<>();
        xml.getBeans().forEach(cfg -> {
            LOGGER.debug("Found <Bean>, identify: " + cfg.identify);
            final BeanFactory factory;
            try {
                final BeanFactory reused = factories.get(cfg.factoryClass);
                if (reused != null) {
                    factory = reused;
                } else {
                    factory = (BeanFactory) Clazz.loadClass(cfg.factoryClass).newInstance();
                    factories.put(cfg.factoryClass, factory);
                }
            } catch (Exception e) {
                LOGGER.error("INVALID_FACTORY_CLASS: " + cfg.factoryClass, e);
                throw new RuntimeException("Load/Instance factory class:" + cfg.factoryClass, e);
            }
            final BeanField field = new BeanField(
                    cfg.identify,
                    cfg.beanClass.isEmpty() ? null : Clazz.loadClass(cfg.beanClass),
                    cfg.initParams,
                    factory);
            IDENTIFY_REGISTER.put(cfg.identify, field);
            // 默认情况下，每个Bean都是Lazy加载模式。
            // 可以通过Bean.preload = true来设置预加载。
            if (cfg.preload) {
                field.loadValue();
                LOGGER.debug("Preload <Bean>, identify: " + cfg.identify);
            }
        });
    }

    static BeanField getField(String identify) {
        final BeanField beanField = IDENTIFY_REGISTER.get(identify);
        if (null == beanField) {
            LOGGER.error("INVALID_IDENTIFY: " + identify);
            throw new IllegalArgumentException("Identify IS NOT a configuration item: " + identify);
        }
        return beanField;
    }

}
