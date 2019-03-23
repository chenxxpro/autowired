package net.nextabc.autowired;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.2
 */
public final class DefaultBeanFactory implements BeanFactory {

    @Override
    public Object create(Class<?> beanType, Map<String, String> nonnullArgs) throws Exception {
        if (beanType == null) {
            throw new IllegalArgumentException("DefaultBeanFactory requires NON-NULL bean type");
        }
        // 创建默认对象策略：
        // 1. 如果参数为空，直接使用newInstance创建；
        // 3. 如果参数只有一个，并且是Map类型，直接将Map参数传入构造器；
        // 2. 如果参数只有一个，尝试基本类型的构造器并解析参数类型来创建；
        // 4. 如果参数有多个，暂不支持；
        if (nonnullArgs.isEmpty()) {
            return beanType.newInstance();
        }

        if (1 == nonnullArgs.size()) {
            // Map类型
            final Constructor<?> mapC = getConstructor(beanType, Map.class);
            if (mapC != null) {
                return mapC.newInstance(nonnullArgs);
            }
            final String value = nonnullArgs.values().iterator().next();
            // String
            final Constructor<?> strC = getConstructor(beanType, String.class);
            if (strC != null) {
                return strC.newInstance(value);
            }
            // Int
            final Constructor<?> intC = getConstructorOr(beanType, int.class, Integer.class);
            if (intC != null) {
                return intC.newInstance(Integer.parseInt(value));
            }
            // Long
            final Constructor<?> longC = getConstructorOr(beanType, long.class, Long.class);
            if (longC != null) {
                return longC.newInstance(Long.parseLong(value));
            }
            // Float
            final Constructor<?> floatC = getConstructorOr(beanType, float.class, Float.class);
            if (floatC != null) {
                return floatC.newInstance(Float.parseFloat(value));
            }
            // Double
            final Constructor<?> doubleC = getConstructorOr(beanType, double.class, Double.class);
            if (doubleC != null) {
                return doubleC.newInstance(Double.parseDouble(value));
            }
            // Boolean
            final Constructor<?> boolC = getConstructorOr(beanType, boolean.class, Boolean.class);
            if (boolC != null) {
                return boolC.newInstance(Boolean.parseBoolean(value));
            }
        }

        throw new UnsupportedOperationException("DefaultBeanFactory not support multi-args constructor: " + beanType);
    }

    private Constructor<?> getConstructor(Class<?> host, Class<?> argType) {
        try {
            return host.getDeclaredConstructor(argType);
        } catch (Exception e) {
            return null;
        }
    }

    private Constructor<?> getConstructorOr(Class<?> host, Class<?>... toTryTypes) {
        for (Class<?> type : toTryTypes) {
            final Constructor<?> c = getConstructor(host, type);
            if (c != null) {
                return c;
            }
        }
        return null;
    }
}
