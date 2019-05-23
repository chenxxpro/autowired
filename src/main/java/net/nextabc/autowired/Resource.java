package net.nextabc.autowired;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 资源文件读取工具类
 *
 * @author 陈永佳 (yoojiachen@gmail.com)
 * @version 0.0.1
 */
public class Resource {

    private static final Logger LOGGER = Logger.getLogger(Resource.class);

    public interface Provider {
        InputStream get() throws Exception;
    }

    private final List<Provider> providers;

    private Resource(List<Provider> providers) {
        this.providers = providers;
    }

    public InputStream getOrNull() {
        for (Provider provider : providers) {
            try {
                final InputStream is = provider.get();
                if (is != null) {
                    return is;
                }
            } catch (Exception e) {
                LOGGER.error("Read from provider", e);
            }
        }
        return null;
    }

    public <T> T getOrNullAs(Function<InputStream, T> fun) {
        final InputStream is = getOrNull();
        if (is != null) {
            return fun.apply(is);
        } else {
            return null;
        }
    }

    public static Resource from(Provider... providers) {
        return new Resource(Arrays.asList(providers));
    }

    public static Provider File(String path) {
        return File(null == path ? null : new File(path));
    }

    public static Provider File(File file) {
        return new FileProvider(file);
    }

    public static Provider Classpath(String resource) {
        return new ClasspathProvider(resource);
    }

    ////

    public static class FileProvider implements Provider {

        private final File file;

        public FileProvider(String path) {
            this(null == path ? null : new File(path));
        }

        public FileProvider(File file) {
            this.file = file;
        }

        @Override
        public InputStream get() throws Exception {
            if (null != file && file.isFile() && file.exists()) {
                return new FileInputStream(file);
            } else {
                return null;
            }
        }
    }

    public static class ClasspathProvider implements Provider {

        private final String resName;

        public ClasspathProvider(String resName) {
            this.resName = resName;
        }

        @Override
        public InputStream get() throws Exception {
            return Clazz.CLASS_LOADER.getResourceAsStream(resName);
        }
    }
}
