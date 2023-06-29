package com.hkl.rpc.loadbalance.enhanced.consistent.hash;

import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author : HK意境
 * @ClassName : ConsistentHashEnhancedServiceLoadBalancer
 * @date : 2023/6/29 21:38
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class ConsistentHashEnhancedServiceLoadBalancer implements ServiceLoadbalancer<ServiceMeta> {


    private static final int VIRTUAL_NODE_SIZE = 10;
    private static final String VIRTUAL_NODE_SPLIT = "#";


    /**
     * 增强型一致性hash 负载均衡策略
     * @param servers 服务列表
     * @param hashCode hash值
     * @param sourceIp 源IP地址
     * @return
     */
    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {

        log.debug("service consumer load balance by enhanced source ip hash weight...");

        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        TreeMap<Integer,ServiceMeta> ring = this.makeConsistentHashRing(servers);
        return this.allocateNode(ring, hashCode, sourceIp);
    }


    private ServiceMeta allocateNode(TreeMap<Integer,ServiceMeta> ring, int hashCode, String sourceIp) {

        // ceilingEntry 方法放回第一个大于或等于指定key的Entry, 不存在则返回 null
        Map.Entry<Integer, ServiceMeta> instanceEntry = ring.ceilingEntry(hashCode);
        if (Objects.isNull(instanceEntry)) {
            instanceEntry = ring.firstEntry();
        }

        if (Objects.isNull(instanceEntry)) {
            throw new RuntimeException("not discover useful service instance, please register a service instance in registry center!");
        }

        return instanceEntry.getValue();
    }


    private TreeMap<Integer, ServiceMeta> makeConsistentHashRing(List<ServiceMeta> servers) {

        TreeMap<Integer,ServiceMeta> ring = new TreeMap<>();

        for (ServiceMeta server : servers) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                ring.put((this.buildServiceInstanceKey(server) + VIRTUAL_NODE_SPLIT + i).hashCode(), server);
            }
        }
        return ring;
    }



    private String buildServiceInstanceKey(ServiceMeta server) {

        return String.join(":",server.getServiceAddress(), String.valueOf(server.getPort()));
    }

}
