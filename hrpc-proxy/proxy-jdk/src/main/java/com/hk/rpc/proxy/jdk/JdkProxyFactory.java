package com.hk.rpc.proxy.jdk;

import com.hk.rpc.proxy.api.BaseProxyFactory;
import com.hk.rpc.proxy.api.consumer.Consumer;
import com.hk.rpc.proxy.api.object.ObjectProxy;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@SPIClass
public class JdkProxyFactory<T> extends BaseProxyFactory<T> {


    /**
     * 获取代理对象
     * @param clazz
     * @return
     */
    @Override
    public <T> T getProxy(Class<T> clazz) {

        log.debug("base jdk proxy...");
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this.objectProxy);
    }
}
