package com.hk.rpc.proxy.api;

import com.hk.rpc.proxy.api.config.ProxyConfig;

/**
 * @author : HK意境
 * @ClassName : ProxyFactory
 * @date : 2023/6/15 23:18
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public interface ProxyFactory {

    /**
     * 获取代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getProxy(Class<T> clazz);


    /**
     * 默认初始化
     * @param config
     * @param <T>
     */
    default <T> void init(ProxyConfig<T> config){}

}
