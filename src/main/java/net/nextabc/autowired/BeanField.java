package net.nextabc.autowired;

import java.util.Map;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.2
 */
class BeanField {

    private final String identify;
    private final Class<?> nullableBeanType;
    private final Map<String, String> initParams;
    private final BeanFactory factory;
    private final LazySupplier<Object> supplier;

    BeanField(String identify, Class<?> nullableBeanType, Map<String, String> nonnullInitParams, BeanFactory factory) {
        this.identify = identify;
        this.nullableBeanType = nullableBeanType;
        this.initParams = nonnullInitParams;
        this.factory = factory;
        this.supplier = new LazySupplier<>(this::createInstance);
    }

    Object loadValue() {
        return supplier.get();
    }

    private Object createInstance() {
        try {
            return factory.create(nullableBeanType, initParams);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create object from factory, identify: " + identify, e);
        }
    }
}
