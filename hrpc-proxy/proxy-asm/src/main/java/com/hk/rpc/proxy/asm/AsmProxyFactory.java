package com.hk.rpc.proxy.asm;

import com.hk.rpc.proxy.api.BaseProxyFactory;
import com.hk.rpc.proxy.asm.proxy.ASMProxy;
import com.hk.rpc.spi.annotation.SPIClass;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : AsmProxyFactory
 * @date : 2023/6/18 18:38
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class AsmProxyFactory<T> extends BaseProxyFactory<T> {

    private ProxyFactory proxyFactory = new ProxyFactory();

    /**
     * 使用 ASM 获取代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Class<T> clazz) {

        log.debug("class={} based by ASM...", clazz.getName());

        try{
            return (T) ASMProxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, this.objectProxy);
        }catch(Exception e){
            log.error("ASM create proxy object error:", e);
        }
        return null;
    }
}
