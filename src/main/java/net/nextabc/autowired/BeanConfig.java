package net.nextabc.autowired;

import java.util.Collections;
import java.util.Map;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
public class BeanConfig {

    final String id;
    final String beanClass;
    final Map<String, String> initParams;
    final String factoryClass;
    final boolean preload;

    BeanConfig(String id, String beanClass, Map<String, String> initParams, String factoryClass, boolean preload) {
        this.id = id;
        this.beanClass = beanClass;
        this.initParams = Collections.unmodifiableMap(initParams);
        this.factoryClass = factoryClass;
        this.preload = preload;
    }

    @Override
    public String toString() {
        return "BeanConfig{" +
                "id='" + id + '\'' +
                ", beanClass=" + beanClass +
                ", initParams=" + initParams +
                ", factoryClass=" + factoryClass +
                ", preload=" + preload +
                '}';
    }
}
