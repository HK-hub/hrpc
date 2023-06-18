package com.hk.rpc.proxy.bytebuddy;

import com.hk.rpc.proxy.api.BaseProxyFactory;
import com.hk.rpc.spi.annotation.SPIClass;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;

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
public class ByteBuddyProxyFactory<T> extends BaseProxyFactory<T> {

    private ProxyFactory proxyFactory = new ProxyFactory();

    /**
     * 使用 cglib 获取代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Class<T> clazz) {

        log.debug("class={} based by ByteBuddy...", clazz.getName());

        try{
            return (T) new ByteBuddy()
                    .subclass(Object.class)
                    .implement(clazz)
                    .intercept(InvocationHandlerAdapter.of(this.objectProxy))
                    .make()
                    .load(ByteBuddyProxyFactory.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        }catch(Exception e){
            log.error("ByteBuddy create proxy object error:", e);
        }
        return null;
    }
}
