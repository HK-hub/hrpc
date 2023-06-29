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
 * @ClassName : HashWeightEnhancedServiceLoadBalancer
 * @date : 2023/6/29 20:45
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class HashWeightEnhancedServiceLoadBalancer extends BaseEnhancedServiceLoadBalancer {


    /**
     * 增强型加权轮询负载均衡算法
     * @param servers 服务列表
     * @param hashCode hash值
     * @param sourceIp 源IP地址
     * @return
     */
    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {

        log.debug("service consumer load balance by enhanced hash weight...");

        servers = this.getWeightServiceMetaList(servers);
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        int index = Math.abs(hashCode) % servers.size();
        return servers.get(index);
    }
}
