package com.hk.rpc.proxy.cglib;

import com.hk.rpc.proxy.api.BaseProxyFactory;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

/**
 * @author : HK意境
 * @ClassName : CglibProxyFactory
 * @date : 2023/6/18 18:38
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class CglibProxyFactory<T> extends BaseProxyFactory<T> {

    private final Enhancer enhancer = new Enhancer();

    /**
     * 使用 cglib 获取代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Class<T> clazz) {

        log.debug("class={} based by cglib proxy...", clazz.getName());
        enhancer.setInterfaces(new Class[]{clazz});
        enhancer.setCallback((InvocationHandler) (o, method, args) -> objectProxy.invoke(o, method, args));

        return (T) enhancer.create();
    }
}
