package com.hk.rpc.proxy.jdk;

import com.hk.rpc.proxy.api.consumer.Consumer;
import com.hk.rpc.proxy.api.object.ObjectProxy;

import java.lang.reflect.Proxy;

/**
 * @author : HK意境
 * @ClassName : JdkProxyFactory
 * @date : 2023/6/15 16:39
 * @description : JDK 动态代理工厂类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class JdkProxyFactory<T> {


    /**
     * 服务版本号
     */
    private String serviceVersion;

    /**
     * 服务分组
     */
    private String serviceGroup;


    /**
     * 默认超时时间: 15秒
     */
    private long timeout = 15000L;


    /**
     * 服务消费者
     */
    private Consumer consumer;


    /**
     * 序列化类型
     */
    private String serializationType;


    /**
     * 是否异步化调用
     */
    private boolean async;

    /**
     * 是否单向调用
     */
    private boolean oneway;


    public JdkProxyFactory(String serviceVersion, String serviceGroup, long timeout, Consumer consumer,
                           String serializationType, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.timeout = timeout;
        this.consumer = consumer;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }


    /**
     * 获取代理对象
     * @param clazz
     * @return
     */
    public T getProxyObject(Class<T> clazz) {

        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                new ObjectProxy<T>(clazz, this.serviceVersion, this.serviceGroup, this.timeout, this.consumer,
                        this.serializationType, this.async, this.oneway));
        return proxy;
    }


}
