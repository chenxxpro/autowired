package net.nextabc.autowired;

/**
 * Bean生命周期
 *
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public interface AutoBean {
    /**
     * Bean已经创建完成
     */
    void onBeanCreated();

    /**
     * Bean开始销毁
     */
    void onBeanDestroy();
}
