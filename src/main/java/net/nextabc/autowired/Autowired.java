package net.nextabc.autowired;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
public final class Autowired<T> {

    static {
        AutoManager.initialize();
        Runtime.getRuntime().addShutdownHook(new Thread(AutoManager::release));
    }

    private final String beanId;

    private Autowired(String beanId) {
        this.beanId = beanId;
    }

    /**
     * 返回Identify值。
     *
     * @return Identify
     */
    public String getBeanId() {
        return beanId;
    }

    /**
     * @see Autowired#getBean()
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public <T0> T0 get() {
        return (T0) getBean();
    }

    /**
     * 获取Autowired管理的Bean对象。
     *
     * @return Bean对象
     */
    @SuppressWarnings("unchecked")
    public T getBean() {
        return (T) AutoManager.getField(this.beanId).loadValue();
    }

    /**
     * @see Autowired#beanId
     */
    @Deprecated()
    public static <T> Autowired<T> identify(String identify) {
        return id(identify);
    }

    /**
     * 基于Id创建Autowired对象.
     *
     * @return Autowired
     */
    public static <T> Autowired<T> id(String identify) {
        return new Autowired<>(identify);
    }

    /**
     * 基于类型名称创建Autowired对象
     *
     * @param type 类型Class
     * @param <T>  类型
     * @return Autowired
     */
    public static <T> Autowired<T> typeOf(Class<T> type) {
        return id(type.getName());
    }
}
