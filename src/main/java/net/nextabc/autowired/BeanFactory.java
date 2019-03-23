package net.nextabc.autowired;

import java.util.Map;

/**
 * 创建Bean对象的工厂函数
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
@FunctionalInterface
public interface BeanFactory {

    /**
     * 创建Bean对象的工厂函数
     *
     * @param nullableBeanType 可能是Null的Bean类型。如果在配置中未设置class属性，此参数为null。
     * @param nonnullArgs      非空初始化参数
     * @return Bean对象
     * @throws Exception 创建对象时发生错误
     */
    Object create(Class<?> nullableBeanType, Map<String, String> nonnullArgs) throws Exception;
}
