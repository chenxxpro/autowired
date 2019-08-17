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
     * 返回Id值。
     *
     * @return Bean Id
     */
    public String id() {
        return id;
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
