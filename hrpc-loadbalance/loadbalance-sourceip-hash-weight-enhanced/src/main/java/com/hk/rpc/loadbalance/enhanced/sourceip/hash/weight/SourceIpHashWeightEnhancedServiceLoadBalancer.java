package com.hk.rpc.loadbalance.enhanced.sourceip.hash.weight;

import com.hk.rpc.loadbalance.api.base.BaseEnhancedServiceLoadBalancer;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author : HK意境
 * @ClassName : SourceIpHashWeightEnhancedServiceLoadBalancer
 * @date : 2023/6/29 20:45
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class SourceIpHashWeightEnhancedServiceLoadBalancer extends BaseEnhancedServiceLoadBalancer {


    /**
     * 增强型加权轮询负载均衡算法
     * @param servers 服务列表
     * @param hashCode hash值
     * @param sourceIp 源IP地址
     * @return
     */
    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {

        log.debug("service consumer load balance by enhanced source ip hash weight...");

        servers = this.getWeightServiceMetaList(servers);
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        // 传入的 IP地址为空，默认返回第一个服务实例
        if (StringUtils.isEmpty(sourceIp)) {
            return servers.get(0);
        }
        int index = Math.abs(sourceIp.hashCode() + hashCode) % servers.size();
        return servers.get(index);
    }
}
