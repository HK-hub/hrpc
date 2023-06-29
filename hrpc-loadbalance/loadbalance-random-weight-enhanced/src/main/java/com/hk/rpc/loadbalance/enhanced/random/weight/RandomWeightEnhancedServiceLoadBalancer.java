package com.hk.rpc.loadbalance.enhanced.random.weight;

import com.hk.rpc.loadbalance.api.base.BaseEnhancedServiceLoadBalancer;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;
import java.util.Random;

/**
 * @author : HK意境
 * @ClassName : RandomWeightEnhancedServiceLoadBalancer
 * @date : 2023/6/26 21:14
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class RandomWeightEnhancedServiceLoadBalancer extends BaseEnhancedServiceLoadBalancer {


    /**
     * 增强型加权随机算法实现负载均衡
     * @param servers 服务列表
     * @param hashCode hash值
     * @return
     */
    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {

        log.debug("service consumer load balance by enhanced random weight...");

        servers = this.getWeightServiceMetaList(servers);
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        Random random = new Random();
        int index = random.nextInt(servers.size());

        return servers.get(index);
    }
}
