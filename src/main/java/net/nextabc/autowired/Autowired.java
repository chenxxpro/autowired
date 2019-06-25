package net.nextabc.autowired;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
public final class Autowired<T> {

    static {
        Registry.create();
        Runtime.getRuntime().addShutdownHook(new Thread(Registry::release));
    }

    private final String id;

    private Autowired(String id) {
        this.id = id;
    }

    /**
     * @see Autowired#id()
     */
    @Deprecated
    public String getBeanId() {
        return id();
    }

    /**
     * 返回Id值。
     *
     * @return Bean Id
     */
    public String id() {
        return id;
    }

    /**
     * @see Autowired#bean()
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public T getBean() {
        return bean();
    }

    /**
     * 获取管理的Bean对象。
     *
     * @return Bean对象
     */
    @SuppressWarnings("unchecked")
    public T bean() {
        return (T) Registry.getField(this.id).loadValue();
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
     * @see Autowired#typeOf(Class)
     */
    @Deprecated
    public static <T> Autowired<T> typeOf(Class<T> type) {
        return id(type.getName());
    }

    /**
     * 基于类型名称创建Autowired对象
     *
     * @param type 类型Class
     * @param <T>  类型
     * @return Autowired
     */
    public static <T> Autowired<T> type(Class<T> type) {
        return id(type.getName());
    }
}
