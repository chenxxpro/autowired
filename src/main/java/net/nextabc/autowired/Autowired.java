package net.nextabc.autowired;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
public final class Autowired {

    static {
        AutoManager.init();
    }

    private final String identify;

    private Autowired(String identify) {
        this.identify = identify;
    }

    /**
     * 返回AutoWrited的Identify标记值。
     *
     * @return Identify
     */
    public String getIdentify() {
        return identify;
    }

    /**
     * 获取AutoWrite管理的值。
     * @return 值对象
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) AutoManager.getField(this.identify).loadValue();
    }

    /**
     * @return Autowired
     */
    public static Autowired identify(String identify) {
        return new Autowired(identify);
    }

}
