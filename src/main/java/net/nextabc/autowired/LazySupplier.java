package net.nextabc.autowired;

import java.util.function.Supplier;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.1
 */
public class LazySupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;

    private volatile T value;

    public LazySupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (value == null) {
            synchronized (this) {
                if (value == null) {
                    value = this.supplier.get();
                }
            }
        }
        return value;
    }
}