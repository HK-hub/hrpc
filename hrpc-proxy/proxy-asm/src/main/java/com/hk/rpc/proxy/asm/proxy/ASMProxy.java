package com.hk.rpc.proxy.asm.proxy;

import com.hk.rpc.proxy.asm.classloader.ASMClassLoader;
import com.hk.rpc.proxy.asm.factory.ASMGenerateProxyFoactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : HK意境
 * @ClassName : ASMProxy
 * @date : 2023/6/18 19:52
 * @description : 作为代理类需要继承的父类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ASMProxy {

    protected InvocationHandler h;

    /**
     * 代理类名计数器
     */
    private static final AtomicInteger counter = new AtomicInteger(1);

    private static final String PROXY_CLASS_PREFIX = "$Proxy";

    public ASMProxy(InvocationHandler handler) {
        this.h = handler;
    }

    /**
     * 获取代理对象实例
     * @param classLoader
     * @param interfaces
     * @param invocationHandler
     * @return
     */
    public static Object newProxyInstance(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler invocationHandler) throws Exception {

        // 生成代理类
        Class<?> proxyClass = generate(interfaces);
        Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
        return constructor.newInstance(invocationHandler);
    }


    /**
     * 生成代理类Class
     * @param interfaces 接口Class
     * @return
     */
    private static Class<?> generate(Class<?>[] interfaces) throws Exception {

        String proxyClassName = PROXY_CLASS_PREFIX + counter.getAndIncrement();
        byte[] codes = ASMGenerateProxyFoactory.generateClass(interfaces, proxyClassName);

        // 使用自定义类加载器来加载字节码
        ASMClassLoader asmClassLoader = new ASMClassLoader();
        asmClassLoader.add(proxyClassName, codes);

        return asmClassLoader.loadClass(proxyClassName);
    }
}
