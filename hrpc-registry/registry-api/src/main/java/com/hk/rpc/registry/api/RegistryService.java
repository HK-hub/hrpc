package com.hk.rpc.registry.api;

import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.registry.api.config.RegistryConfig;

import java.io.IOException;

/**
 * @author : HK意境
 * @ClassName : RegistryService
 * @date : 2023/6/16 14:40
 * @description : 服务注册与发现接口
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public interface RegistryService {

    /**
     * 服务注册
     * @param serviceMeta 服务元数据
     */
    public void register(ServiceMeta serviceMeta) throws Exception;


    /**
     * 服务取消注册
     * @param serviceMeta 服务元数据
     * @throws Exception
     */
    public void unregister(ServiceMeta serviceMeta) throws Exception;


    /**
     * 服务发现
     * @param serviceName 服务名称
     * @param invokerHashCode HashCode 值
     * @param sourceIp 源IP地址
     * @return {@link ServiceMeta} 服务元数据
     * @throws Exception
     */
    public ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIp) throws Exception;


    /**
     * 服务销毁
     * @throws IOException 抛出异常
     */
    public void destroy() throws IOException;



    /**
     * 默认初始化方法
     * @param registryConfig
     */
    default void init(RegistryConfig registryConfig) throws Exception {

    }



}
