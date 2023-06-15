package com.hk.rpc.consumer;

import com.hk.rpc.consumer.common.RpcConsumer;
import com.hk.rpc.proxy.api.async.IAsyncObjectProxy;
import com.hk.rpc.proxy.api.object.ObjectProxy;
import com.hk.rpc.proxy.jdk.JdkProxyFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : RpcClient
 * @date : 2023/6/15 20:10
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@Data
public class RpcClient {

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


    public RpcClient(String serviceVersion, String serviceGroup, long timeout, String serializationType, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.timeout = timeout;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }


    /**
     * 创建代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> clazz) {

        JdkProxyFactory<T> jdkProxyFactory = new JdkProxyFactory<T>(serviceVersion, serviceGroup, timeout,
                RpcConsumer.getInstance(), serializationType, async, oneway);
        return jdkProxyFactory.getProxyObject(clazz);
    }

    /**
     * 创建异步调用代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> IAsyncObjectProxy createAsync(Class<T> clazz) {

        return new ObjectProxy<T>(clazz, serviceVersion, serviceGroup, timeout,
                RpcConsumer.getInstance(), serializationType, async, oneway);
    }


    public void shutdown() {
        RpcConsumer.getInstance().close();
    }


}
