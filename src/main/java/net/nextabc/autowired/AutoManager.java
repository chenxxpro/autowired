package net.nextabc.autowired;


import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
class AutoManager {

    private static final Logger LOGGER = Logger.getLogger(AutoManager.class);

    private static final Map<String, BeanField> IDENTIFY_REGISTER = new HashMap<>();

    private AutoManager() {
    }

    static void init() {
        final XmlConfig xml = new XmlConfig();
        // 从配置文件中加载
        final Map<String, BeanFactory> factories = new HashMap<>(10);
        xml.getBeans().forEach(cfg -> {
            final String beanId;
            if (cfg.id.isEmpty()) {
                if (cfg.beanClass.isEmpty()) {
                    throw new IllegalArgumentException("Bean.id OR Bean.class MUST BE specified");
                }
                beanId = cfg.beanClass;
                LOGGER.debug("Found <Bean>, USING CLASS NAME as id: " + beanId);
            } else {
                beanId = cfg.id;
                LOGGER.debug("Found <Bean>, id: " + beanId);
            }
            // Identify或者Class类型不可重复
            if (IDENTIFY_REGISTER.containsKey(beanId)) {
                throw new IllegalArgumentException("Duplicated id: " + beanId);
            }

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
                    beanId,
                    cfg.beanClass.isEmpty() ? null : Clazz.loadClass(cfg.beanClass),
                    cfg.initParams,
                    factory);
            IDENTIFY_REGISTER.put(beanId, field);
            // 默认情况下，每个Bean都是Lazy加载模式。
            // 可以通过Bean.preload = true来设置预加载。
            if (cfg.preload) {
                field.loadValue();
                LOGGER.debug("Preload <Bean>, id: " + beanId);
            }
        });
    }

    static BeanField getField(String identify) {
        final BeanField beanField = IDENTIFY_REGISTER.get(identify);
        if (null == beanField) {
            LOGGER.error("INVALID_IDENTIFY: " + identify);
            throw new IllegalArgumentException("Identify IS NOT a bean item: " + identify);
        }
        return beanField;
    }

}
