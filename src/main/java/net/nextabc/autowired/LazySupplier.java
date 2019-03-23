package net.nextabc.autowired;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.1
 */
public class LazySupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;

    private final AtomicBoolean ready = new AtomicBoolean(false);
    private T value;

    public LazySupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (!ready.getAndSet(true)) {
            value = this.supplier.get();
        }
        return value;
    }
}