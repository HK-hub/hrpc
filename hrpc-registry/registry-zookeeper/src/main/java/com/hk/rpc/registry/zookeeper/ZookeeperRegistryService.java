package com.hk.rpc.registry.zookeeper;

import com.hk.rpc.common.helper.RpcServiceHelper;
import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.loadbalance.random.RandomServiceLoadbalancer;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.registry.api.RegistryService;
import com.hk.rpc.registry.api.config.RegistryConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author : HK意境
 * @ClassName : ZookeeperRegistryService
 * @date : 2023/6/16 14:51
 * @description : 基于 Zookeeper 实现的服务注册与发现
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ZookeeperRegistryService implements RegistryService {

    /**
     * 初始化CuratorFramework 客户端时，进行重试连接的间隔时间
     */
    public static final int BASE_SLEEP_TIME_MS = 1000;


    /**
     * 重试连接的最大重试次数
     */
    public static final int MAX_RETRIES = 3;

    /**
     * 服务注册到 Zookeeper 的根路径
     */
    public static final String ZK_BASH_PATH = "/h_rpc";

    /**
     * 服务注册与发现的实例
     */
    private ServiceDiscovery<ServiceMeta> serviceDiscovery;

    /**
     * 复制均衡器
     */
    private ServiceLoadbalancer<ServiceInstance<ServiceMeta>> serviceLoadbalancer;


    /**
     * 初始化注册服务
     * @param registryConfig
     */
    @Override
    public void init(RegistryConfig registryConfig) throws Exception {

        CuratorFramework client = CuratorFrameworkFactory.newClient(registryConfig.getRegistryAddress(),
                new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();

        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .basePath(ZK_BASH_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMeta.class))
                .build();

        // 负载均衡器
        this.serviceLoadbalancer = new RandomServiceLoadbalancer<>();
        // 启动服务发现注册
        this.serviceDiscovery.start();
    }


    /**
     * 注册服务
     * @param serviceMeta 服务元数据
     * @throws Exception
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {

        // 构建注册服务实体
        ServiceInstanceBuilder<ServiceMeta> builder = ServiceInstance.builder();
        ServiceInstance<ServiceMeta> metaServiceInstance = builder.name(RpcServiceHelper.locationService(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()))
                .address(serviceMeta.getServiceAddress())
                .port(serviceMeta.getPort())
                .payload(serviceMeta)
                .build();

        // 注册服务提供者的实例
        this.serviceDiscovery.registerService(metaServiceInstance);
    }


    /**
     * 服务移除,注销
     * @param serviceMeta 服务元数据
     * @throws Exception
     */
    @Override
    public void unregister(ServiceMeta serviceMeta) throws Exception {

        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(serviceMeta.getServiceName())
                .address(serviceMeta.getServiceAddress())
                .port(serviceMeta.getPort())
                .payload(serviceMeta)
                .build();
        // 移除服务
        this.serviceDiscovery.unregisterService(serviceInstance);
    }


    /**
     * 服务发现方法
     * @param serviceName 服务名称
     * @param invokerHashCode HashCode 值
     * @return
     * @throws Exception
     */
    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {

        // 获取服务集合
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = this.serviceDiscovery.queryForInstances(serviceName);

        // 选择其中一个
        ServiceInstance<ServiceMeta> instance = this.selectOneServiceInstance((List<ServiceInstance<ServiceMeta>>)serviceInstances, invokerHashCode);

        if (Objects.nonNull(instance)) {
            return instance.getPayload();
        }
        return null;
    }


    /**
     * 销毁服务，断开连接
     * @throws IOException
     */
    @Override
    public void destroy() throws IOException {
        this.serviceDiscovery.close();
    }


    /**
     * 按照某则策略 选择服务集合中的一个
     * @param serviceInstances 服务实例集合
     * @return {@link ServiceInstance<ServiceMeta>}
     */
    private ServiceInstance<ServiceMeta> selectOneServiceInstance(List<ServiceInstance<ServiceMeta>> serviceInstances, int hashCode) {

        // 使用负载均衡器进行选择
        return this.serviceLoadbalancer.select(serviceInstances, hashCode);
    }


}
