package com.hk.rpc.spi.loader;

import com.hk.rpc.spi.annotation.SPI;
import com.hk.rpc.spi.annotation.SPIClass;
import com.hk.rpc.spi.factory.ExtensionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : HK意境
 * @ClassName : ExtensionLoader
 * @date : 2023/6/17 20:22
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class ExtensionLoader<T> {


    /**
     * SPI 扩展默认路径
     */
    private static final String SERVICES_DIRECTORY = "META-INF/services/";
    /**
     * SPI 扩展自定义路径
     */
    private static final String HK_DIRECTORY = "META-INF/hk/";
    private static final String HK_DIRECTORY_EXTERNAL = "META-INF/hk/external/";
    private static final String HK_DIRECTORY_INTERNAL = "META-INF/hk/internal/";

    private static final String[] SPI_DIRECTORIES = new String[]{SERVICES_DIRECTORY, HK_DIRECTORY, HK_DIRECTORY_EXTERNAL, HK_DIRECTORY_INTERNAL};

    private static final Map<Class<?>, ExtensionLoader<?>> LOADER_MAP = new ConcurrentHashMap<>();

    private final Class<T> clazz;

    private final ClassLoader classLoader;

    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> spiClassInstances = new ConcurrentHashMap<>();

    private String cachedDefaultName;

    /**
     * Instantiates a new Extension loader.
     *
     * @param clazz the clazz.
     */
    private ExtensionLoader(final Class<T> clazz, final ClassLoader cl) {
        this.clazz = clazz;
        this.classLoader = cl;
        if (!Objects.equals(clazz, ExtensionFactory.class)) {
            ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getExtensionClasses();
        }
    }



    /**
     * Gets extension loader.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the extension loader
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> clazz) {
        return getExtensionLoader(clazz, ExtensionLoader.class.getClassLoader());
    }


    /**
     * 获取扩展加载器
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param cl    the cl
     * @return the extension loader.
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> clazz, final ClassLoader cl) {

        Objects.requireNonNull(clazz, "extension clazz is null");

        // 是否接口
        if (BooleanUtils.isFalse(clazz.isInterface())) {
            throw new IllegalArgumentException("extension clazz (" + clazz + ") is not interface!");
        }

        // 是否标注 @SPI 注解
        if (BooleanUtils.isFalse(clazz.isAnnotationPresent(SPI.class))) {
            throw new IllegalArgumentException("extension clazz (" + clazz + ") without @" + SPI.class + " Annotation");
        }

        // 获取缓存的扩展类加载器
        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) LOADER_MAP.get(clazz);
        if (Objects.nonNull(extensionLoader)) {
            return extensionLoader;
        }

        LOADER_MAP.putIfAbsent(clazz, new ExtensionLoader<T>(clazz, cl));
        return (ExtensionLoader<T>) LOADER_MAP.get(clazz);
    }




    /**
     * 直接获取想要的类实例
     * @param clazz clazz 类接口的实例
     * @param name SPI 名称
     * @param <T>
     * @return 泛型实例
     */
    public static <T> T getExtension(final Class<T> clazz, String name) {

        if (StringUtils.isEmpty(name)) {
            return getExtensionLoader(clazz).getDefaultSpiClassInstance();
        }

        return getExtensionLoader(clazz).getSpiClassInstance(name);
    }


    /**
     * 获取默认 SPI 类实例
     * @return
     */
    public T getDefaultSpiClassInstance() {

        getExtensionClasses();
        if (StringUtils.isBlank(cachedDefaultName)) {
            return null;
        }

        return getSpiClassInstance(cachedDefaultName);
    }


    /**
     * 获取 SPI 类实例
     * @param name
     * @return
     */
    public T getSpiClassInstance(String name) {

        if (StringUtils.isBlank(name)) {
            throw new NullPointerException("get spi class name is null");
        }

        Holder<Object> holder = cachedInstances.get(name);
        if (Objects.isNull(holder)) {
            cachedInstances.put(name, new Holder<>());
            holder = cachedInstances.get(name);
        }

        Object value = holder.getValue();
        if (Objects.isNull(value)) {
            synchronized (cachedInstances) {
                value = holder.getValue();
                if (Objects.isNull(value)) {
                    value = createExtension(name);
                    holder.setValue(value);
                }
            }

        }
        return (T) value;
    }


    @SuppressWarnings("unchecked")
    private T createExtension(String name) {

        Class<?> aClass = getExtensionClasses().get(name);
        if (Objects.isNull(aClass)) {
            throw new IllegalArgumentException("name is error");
        }
        Object o = spiClassInstances.get(aClass);
        if (Objects.isNull(o)) {
            try {
                spiClassInstances.putIfAbsent(aClass, aClass.newInstance());
                o = spiClassInstances.get(aClass);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Extension instance(name: " + name + ", class: "
                        + aClass + ")  could not be instantiated: " + e.getMessage(), e);

            }
        }
        return (T) o;
    }



    /**
     * Gets extension classes.
     *
     * @return the extension classes
     */
    public Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.getValue();
        if (Objects.isNull(classes)) {
            synchronized (cachedClasses) {
                classes = cachedClasses.getValue();
                if (Objects.isNull(classes)) {
                    classes = loadExtensionClass();
                    cachedClasses.setValue(classes);
                }
            }
        }
        return classes;
    }


    private Map<String, Class<?>> loadExtensionClass() {
        SPI annotation = clazz.getAnnotation(SPI.class);
        if (Objects.nonNull(annotation)) {
            String value = annotation.value();
            if (StringUtils.isNotBlank(value)) {
                cachedDefaultName = value;
            }
        }
        Map<String, Class<?>> classes = new HashMap<>(16);
        loadDirectory(classes);
        return classes;
    }


    private void loadDirectory(final Map<String, Class<?>> classes) {
        for (String directory : SPI_DIRECTORIES){
            String fileName = directory + clazz.getName();
            try {
                Enumeration<URL> urls = Objects.nonNull(this.classLoader) ? classLoader.getResources(fileName)
                        : ClassLoader.getSystemResources(fileName);
                if (Objects.nonNull(urls)) {
                    while (urls.hasMoreElements()) {
                        URL url = urls.nextElement();
                        loadResources(classes, url);
                    }
                }
            } catch (IOException t) {
                log.error("load extension class error {}", fileName, t);
            }
        }
    }

    private void loadResources(final Map<String, Class<?>> classes, final URL url) throws IOException {
        try (InputStream inputStream = url.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            properties.forEach((k, v) -> {
                String name = (String) k;
                String classPath = (String) v;
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(classPath)) {
                    try {
                        loadClass(classes, name, classPath);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("load extension resources error", e);
                    }
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("load extension resources error", e);
        }
    }


    private void loadClass(final Map<String, Class<?>> classes,
                           final String name, final String classPath) throws ClassNotFoundException {
        Class<?> subClass = Objects.nonNull(this.classLoader) ? Class.forName(classPath, true, this.classLoader) : Class.forName(classPath);
        if (!clazz.isAssignableFrom(subClass)) {
            throw new IllegalStateException("load extension resources error," + subClass + " subtype is not of " + clazz);
        }
        if (!subClass.isAnnotationPresent(SPIClass.class)) {
            throw new IllegalStateException("load extension resources error," + subClass + " without @" + SPIClass.class + " annotation");
        }
        Class<?> oldClass = classes.get(name);
        if (Objects.isNull(oldClass)) {
            classes.put(name, subClass);
        } else if (!Objects.equals(oldClass, subClass)) {
            throw new IllegalStateException("load extension resources error,Duplicate class " + clazz.getName() + " name " + name + " on " + oldClass.getName() + " or " + subClass.getName());
        }
    }




    /**
     * The type Holder.
     *
     * @param <T> the type parameter.
     */
    public static class Holder<T> {

        private volatile T value;

        /**
         * Gets value.
         *
         * @return the value
         */
        public T getValue() {
            return value;
        }

        /**
         * Sets value.
         *
         * @param value the value
         */
        public void setValue(final T value) {
            this.value = value;
        }
    }

}
