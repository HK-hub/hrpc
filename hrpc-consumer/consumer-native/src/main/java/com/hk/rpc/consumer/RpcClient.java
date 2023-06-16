package com.hk.rpc.consumer;

import com.hk.rpc.common.exception.RegistryException;
import com.hk.rpc.consumer.common.RpcConsumer;
import com.hk.rpc.proxy.api.ProxyFactory;
import com.hk.rpc.proxy.api.async.IAsyncObjectProxy;
import com.hk.rpc.proxy.api.config.ProxyConfig;
import com.hk.rpc.proxy.api.object.ObjectProxy;
import com.hk.rpc.proxy.jdk.JdkProxyFactory;
import com.hk.rpc.registry.api.RegistryService;
import com.hk.rpc.registry.api.config.RegistryConfig;
import com.hk.rpc.registry.zookeeper.ZookeeperRegistryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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


    private RegistryService registryService;

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


    public RpcClient(String registryAddress, String registryType, String serviceVersion, String serviceGroup, long timeout,
                     String serializationType, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.timeout = timeout;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;

        this.registryService = this.createRegistryService(registryAddress, registryType);
    }

    /**
     * 创建服务注册与发现接口
     * @param registryAddress
     * @param registryType
     * @return
     */
    private RegistryService createRegistryService(String registryAddress, String registryType) {

        if (StringUtils.isEmpty(registryType)) {
            throw new IllegalArgumentException("Invalid registry type: " + registryType);
        }

        // TODO 后续SPI扩展
        RegistryService registryService  = new ZookeeperRegistryService();
        try{
            registryService.init(new RegistryConfig(registryAddress, registryType));
        }catch(Exception e){
            log.error("rpc client init registry service throw exception: ", e);
            throw new RegistryException(e.getMessage(), e);
        }

        return registryService;
    }


    /**
     * 创建代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> clazz) {

        ProxyFactory proxyFactory = new JdkProxyFactory<T>();
        // 初始化工厂
        proxyFactory.init(new ProxyConfig<>(clazz, this.serviceVersion, this.serviceGroup, this.timeout, this.registryService, RpcConsumer.getInstance(), this.serializationType, this.async, this.oneway));
        // 获取代理对象
        return proxyFactory.getProxy(clazz);
    }

    /**
     * 创建异步调用代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> IAsyncObjectProxy createAsync(Class<T> clazz) {

        return new ObjectProxy<T>(clazz, serviceVersion, serviceGroup, timeout, this.registryService,
                RpcConsumer.getInstance(), serializationType, async, oneway);
    }


    public void shutdown() {
        RpcConsumer.getInstance().close();
    }


}
