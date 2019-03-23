package net.nextabc.autowired;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
public class Clazz {

    static final ClassLoader CLASS_LOADER;

    static {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        if (cl == null) {
            cl = Clazz.class.getClassLoader();
        }
        CLASS_LOADER = cl;
    }

    private Clazz() {
    }

    @SuppressWarnings("unchecked")
    static <T> Class<T> loadClass(String className) {
        try {
            return (Class<T>) CLASS_LOADER.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class: " + className, e);
        }
    }

}
