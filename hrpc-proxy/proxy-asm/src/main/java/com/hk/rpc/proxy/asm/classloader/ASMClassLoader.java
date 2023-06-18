package com.hk.rpc.proxy.asm.classloader;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : HK意境
 * @ClassName : ASMClassLoader
 * @date : 2023/6/18 19:46
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class ASMClassLoader extends ClassLoader{

    private final Map<String, byte[]> classMap = new ConcurrentHashMap<>();



    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        if (classMap.containsKey(name)) {
            byte[] bytes = classMap.get(name);
            classMap.remove(name);

            return defineClass(name, bytes, 0, bytes.length);
        }

        return super.findClass(name);
    }


    public void add(String name, byte[] bytes) {
        this.classMap.put(name, bytes);
    }
}
