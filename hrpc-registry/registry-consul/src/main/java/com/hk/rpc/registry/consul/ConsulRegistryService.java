package com.hk.rpc.registry.consul;

import com.google.common.net.HostAndPort;
import com.hk.rpc.common.helper.RpcServiceHelper;
import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.registry.api.RegistryService;
import com.hk.rpc.registry.api.config.RegistryConfig;
import com.hk.rpc.spi.annotation.SPIClass;
import com.hk.rpc.spi.loader.ExtensionLoader;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.Service;
import com.orbitz.consul.model.health.ServiceHealth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : HK意境
 * @ClassName : ConsulRegistryService
 * @date : 2023/7/2 23:06
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class ConsulRegistryService implements RegistryService {


    /**
     * 客户端连接
     */
    private Consul client;

    /**
     * 服务注册客户端
     */
    private AgentClient agentClient;

    /**
     * 服务发现客户端
     */
    private HealthClient healthClient;


    /**
     * 负载均衡器
     */
    private ServiceLoadbalancer<ServiceMeta> loadBalancer;



    /**
     * 初始化
     * @param registryConfig
     * @throws Exception
     */
    @Override
    public void init(RegistryConfig registryConfig) throws Exception {

        // 连接Consul注册中心
        String registryAddress = registryConfig.getRegistryAddress();
        this.client = Consul.builder()
                .withHostAndPort(HostAndPort.fromString(registryAddress))
                .build();
        this.agentClient = this.client.agentClient();
        this.healthClient = this.client.healthClient();

        // 负载均衡器
        this.loadBalancer = ExtensionLoader.getExtension(ServiceLoadbalancer.class, registryConfig.getRegistryLoadBalanceType());
    }


    /**
     * 服务实例注册
     * @param serviceMeta 服务元数据
     * @throws Exception
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {

        // 计算服务实例key
        String serviceKey = RpcServiceHelper.locationService(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup());
        // 计算服务实例唯一id
        int port = serviceMeta.getPort();
        String serviceId = this.buildServiceId(serviceMeta);
        // 构建注册元数据
        ImmutableRegistration registration = ImmutableRegistration.builder()
                .id(serviceId)
                .name(serviceKey)
                .port(port)
                // 租约 60 秒
                .check(Registration.RegCheck.ttl(60))
                .tags(Collections.singletonList(serviceMeta.getServiceName()))
                .meta(BeanUtils.describe(serviceMeta))
                .build();
        // 注册服务实例
        this.agentClient.register(registration);

    }


    /**
     * 构造服务实例id
     * @param serviceMeta
     * @return
     */
    private String buildServiceId(ServiceMeta serviceMeta) {

        // 计算服务实例key
        String serviceKey = RpcServiceHelper.locationService(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup());
        // 计算服务实例唯一id
        int port = serviceMeta.getPort();
        String serviceId = serviceKey.concat(serviceMeta.getServiceAddress())
                .concat(":")
                .concat(String.valueOf(port));

        return serviceId;
    }


    /**
     * 注销服务实例
     * @param serviceMeta 服务元数据
     * @throws Exception
     */
    @Override
    public void unregister(ServiceMeta serviceMeta) throws Exception {

        String serviceId = this.buildServiceId(serviceMeta);
        this.agentClient.deregister(serviceId);
    }


    /**
     * 服务发现
     * @param serviceName 服务名称
     * @param invokerHashCode HashCode 值
     * @param sourceIp 源IP地址
     * @return
     * @throws Exception
     */
    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIp) throws Exception {

        // 获取健康服务实例
        List<ServiceHealth> healthList = this.healthClient.getHealthyServiceInstances(serviceName).getResponse();
        List<ServiceMeta> serviceMetaList = healthList.stream().map(healthyService -> {
            Service service = healthyService.getService();
            Map<String, String> meta = service.getMeta();
            ServiceMeta serviceMeta = new ServiceMeta();
            try {
                BeanUtils.populate(serviceMeta, meta);
            } catch (Exception e) {
                log.error("failed to populate meta={} to ServiceMeta instance:", meta, e);
                return null;
            }
            return serviceMeta;
        }).collect(Collectors.toList());

        // 负载均衡
        ServiceMeta select = this.loadBalancer.select(serviceMetaList, hashCode(), sourceIp);
        log.debug("load balancer select serviceMeta list element is:{}", select.toString());

        return select;
    }


    /**
     * 服务提供者销毁
     * @throws IOException
     */
    @Override
    public void destroy() throws IOException {

    }

}
