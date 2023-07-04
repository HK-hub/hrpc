package com.hk.rpc.registry.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hk.rpc.common.helper.RpcServiceHelper;
import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.registry.api.RegistryService;
import com.hk.rpc.registry.api.config.RegistryConfig;
import com.hk.rpc.spi.annotation.SPIClass;
import com.hk.rpc.spi.loader.ExtensionLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : HK意境
 * @ClassName : NacosRegistryService
 * @date : 2023/7/3 17:31
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class NacosRegistryService implements RegistryService {

    /**
     * 服务注册与发现连接客户端
     */
    private NamingService namingService;

    /**
     * 负载均衡器
     */
    private ServiceLoadbalancer<ServiceMeta> serviceLoadbalancer;


    /**
     * 初始化注册中心连接
     * @param registryConfig
     * @throws Exception
     */
    @Override
    public void init(RegistryConfig registryConfig) throws Exception {


        this.namingService = NacosFactory.createNamingService(registryConfig.getRegistryAddress());
        this.serviceLoadbalancer = ExtensionLoader.getExtension(ServiceLoadbalancer.class, registryConfig.getRegistryLoadBalanceType());
    }


    /**
     * 服务实例注册
     * @param serviceMeta 服务元数据
     * @throws Exception
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {

        // 构建服务实例
        Instance instance = this.buildServiceInstance(serviceMeta);

        // 服务注册
        this.namingService.registerInstance(instance.getServiceName(),  instance);
    }

    /**
     * 构建服务实例
     * @param serviceMeta
     * @return
     */
    private Instance buildServiceInstance(ServiceMeta serviceMeta) throws Exception {

        Instance instance = new Instance();
        instance.setServiceName(RpcServiceHelper.locationService(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()));
        instance.setIp(serviceMeta.getServiceAddress());
        instance.setPort(serviceMeta.getPort());
        instance.setWeight(serviceMeta.getWeight());

        instance.setMetadata(BeanUtils.describe(serviceMeta));
        return instance;
    }


    /**
     * 销毁服务实例
     * @param serviceMeta 服务元数据
     * @throws Exception
     */
    @Override
    public void unregister(ServiceMeta serviceMeta) throws Exception {

        Instance instance = this.buildServiceInstance(serviceMeta);
        this.namingService.deregisterInstance(instance.getServiceName(), instance);
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

        // 获取所有服务实例
        List<Instance> allInstances = this.namingService.getAllInstances(serviceName);
        List<ServiceMeta> serviceMetaList = allInstances.stream().map(instance -> {
            ServiceMeta serviceMeta = new ServiceMeta();
            try {
                BeanUtils.populate(serviceMeta, instance.getMetadata());
            } catch (Exception e) {
                log.error("populate serviceMeta by instance={},error:", instance.toString(), e);
            }
            return serviceMeta;
        }).collect(Collectors.toList());

        // 负载均衡
        ServiceMeta select = this.serviceLoadbalancer.select(serviceMetaList, hashCode(), sourceIp);
        log.debug("load balancer select serviceMeta list element is:{}", select.toString());

        return select;
    }


    /**
     * 服务实例销毁
     * @throws IOException
     */
    @Override
    public void destroy() throws IOException {

        try {
            this.namingService.shutDown();
        } catch (Exception e) {
            log.error("shutdown service instance error:", e);
        }
    }


}
