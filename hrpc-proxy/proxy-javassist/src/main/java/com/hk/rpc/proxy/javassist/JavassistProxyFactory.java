package com.hk.rpc.proxy.javassist;

import com.hk.rpc.proxy.api.BaseProxyFactory;
import com.hk.rpc.spi.annotation.SPIClass;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : JavassistProxyFactory
 * @date : 2023/6/18 18:38
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class JavassistProxyFactory<T> extends BaseProxyFactory<T> {

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

        log.debug("class={} based by javassist...", clazz.getName());

        try{
            // 设置代理类的父类
            this.proxyFactory.setInterfaces(new Class[]{clazz});
            this.proxyFactory.setHandler((self, thisMethod, proceed, args) -> objectProxy.invoke(self, thisMethod, args));

            // 通过字节码技术动态创建子类实例
            return (T) this.proxyFactory.createClass().newInstance();
        }catch(Exception e){
            log.error("javassist create proxy object error:", e);
        } return null;
    }
}
