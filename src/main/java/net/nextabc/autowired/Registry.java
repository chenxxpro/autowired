package net.nextabc.autowired;


import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.2.0
 */
class Registry {

    private static final Logger LOGGER = Logger.getLogger(Registry.class);

    private static final Map<String, Meta> REGISTER = new HashMap<>();
    private static final AtomicBoolean INITED = new AtomicBoolean(false);

    private Registry() {
    }

    static void create() {
        if (INITED.getAndSet(true)) {
            return;
        }
        final XmlConfig xml = new XmlConfig();
        // 从配置文件中加载
        final Map<String, BeanFactory> reusedFactories = new HashMap<>(10);
        xml.getXmlBeanConfigs().forEach(cfg -> {
            final String beanId;
            if (cfg.id.isEmpty()) {
                if (cfg.beanClass.isEmpty()) {
                    throw new IllegalArgumentException("Bean.id OR Bean.class MUST BE specified");
                }
                beanId = cfg.beanClass;
                LOGGER.debug("Found <Bean>, using <CLASS NAME> as bean id: " + beanId);
            } else {
                beanId = cfg.id;
                LOGGER.debug("Found <Bean>, bean id: " + beanId);
            }
            // Identify或者Class类型不可重复
            if (REGISTER.containsKey(beanId)) {
                throw new IllegalArgumentException("Duplicated bean id: " + beanId);
            }

            final BeanFactory factory;
            try {
                final BeanFactory reused = reusedFactories.get(cfg.factoryClass);
                if (reused != null) {
                    factory = reused;
                } else {
                    factory = (BeanFactory) Clazz.loadClass(cfg.factoryClass).newInstance();
                    reusedFactories.put(cfg.factoryClass, factory);
                }
            } catch (Exception e) {
                LOGGER.error("INVALID_FACTORY_CLASS: " + cfg.factoryClass, e);
                throw new RuntimeException("Load/Instance factory class:" + cfg.factoryClass, e);
            }

            final Meta field = new Meta(
                    beanId,
                    cfg.beanClass.isEmpty() ? null : Clazz.loadClass(cfg.beanClass),
                    cfg.initParams,
                    factory);
            REGISTER.put(beanId, field);
            // 默认情况下，每个Bean都是Lazy加载模式。
            // 可以通过Bean.preload = true来设置预加载。
            if (cfg.preload) {
                field.loadValue();
                LOGGER.debug("Preload <Bean>, bean id: " + beanId);
            }
        });
    }

    static Meta getField(String identify) {
        final Meta meta = REGISTER.get(identify);
        if (null == meta) {
            LOGGER.error("INVALID_BEAN_ID[BEAN_NOT_FOUND]: " + identify);
            throw new IllegalArgumentException("Id IS NOT a bean item: " + identify);
        }
        return meta;
    }

    static void release() {
        final Set<String> keys = new HashSet<>(REGISTER.keySet());
        keys.forEach(key -> REGISTER.remove(key).releaseValue());
    }
}
