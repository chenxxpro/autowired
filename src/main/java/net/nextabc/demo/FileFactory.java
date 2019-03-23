package net.nextabc.demo;

import net.nextabc.autowired.BeanFactory;

import java.io.File;
import java.util.Map;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
public class FileFactory implements BeanFactory {

    @Override
    public Object create(Class<?> nullableBeanType, Map<String, String> nonnullArgs) throws Exception {
        final Object path = nonnullArgs.get("path");
        if (path == null) {
            throw new IllegalArgumentException("init-param[path] is required");
        }
        return new File(path.toString());
    }
}
