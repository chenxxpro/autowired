package net.nextabc.autowired;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

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
                InputStream is = provider.get();
                if (is != null) {
                    return is;
                }
            } catch (Exception e) {
                LOGGER.error("Read from provider", e);
            }
        }
        return null;
    }

    public static Resource from(Provider... providers) {
        return new Resource(Arrays.asList(providers));
    }

    public static Provider File(String path) {
        return File(new File(path));
    }

    public static Provider File(File file) {
        return new FileProvider(file);
    }

    public static Provider Classpath(String resource){
        return new ClasspathProvider(resource);
    }

    ////

    public static class FileProvider implements Provider {

        private final java.io.File file;

        public FileProvider(String path) {
            this(new java.io.File(path));
        }

        public FileProvider(java.io.File file) {
            this.file = file;
        }

        @Override
        public InputStream get() throws Exception {
            if (file.isFile() && file.exists()) {
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
