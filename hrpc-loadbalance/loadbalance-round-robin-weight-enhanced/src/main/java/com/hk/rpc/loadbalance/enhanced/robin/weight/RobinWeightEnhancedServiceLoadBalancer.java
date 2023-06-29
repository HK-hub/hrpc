package com.hk.rpc.loadbalance.enhanced.robin.weight;

import com.hk.rpc.loadbalance.api.base.BaseEnhancedServiceLoadBalancer;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : HK意境
 * @ClassName : RobinWeightEnhancedServiceLoadBalancer
 * @date : 2023/6/29 20:45
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class RobinWeightEnhancedServiceLoadBalancer extends BaseEnhancedServiceLoadBalancer {


    private final static AtomicInteger counter = new AtomicInteger(0);

    /**
     * 增强型加权轮询负载均衡算法
     * @param servers 服务列表
     * @param hashCode hash值
     * @param sourceIp 源IP地址
     * @return
     */
    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {

        log.debug("service consumer load balance by enhanced round robin weight...");

        servers = this.getWeightServiceMetaList(servers);
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        int index = counter.getAndIncrement();
        if (index >= Integer.MAX_VALUE - 10000) {
            counter.set(0);
        }

        return servers.get(index % servers.size());
    }
}
