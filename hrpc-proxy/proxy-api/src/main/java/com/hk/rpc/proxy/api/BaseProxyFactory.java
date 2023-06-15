package com.hk.rpc.proxy.api;

import com.hk.rpc.proxy.api.config.ProxyConfig;
import com.hk.rpc.proxy.api.object.ObjectProxy;

/**
 * @author : HK意境
 * @ClassName : BaseProxyFactory
 * @date : 2023/6/15 23:20
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public abstract class BaseProxyFactory<T> implements ProxyFactory{

    protected ObjectProxy<T> objectProxy;


    /**
     * 初始化
     * @param config
     * @param <T>
     */
    @Override
    public <T> void init(ProxyConfig<T> config) {

        this.objectProxy = new ObjectProxy(config.getClazz(), config.getServiceVersion(), config.getServiceGroup(), config.getTimeout(), config.getConsumer(),
                config.getSerializationType(), config.isAsync(), config.isOneway());
    }
}
