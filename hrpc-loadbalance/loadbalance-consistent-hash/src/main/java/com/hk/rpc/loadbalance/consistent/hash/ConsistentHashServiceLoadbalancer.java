package com.hk.rpc.loadbalance.consistent.hash;

import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author : HK意境
 * @ClassName : ConsistentHashServiceLoadbalancer
 * @date : 2023/6/27 16:03
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class ConsistentHashServiceLoadbalancer implements ServiceLoadbalancer<ServiceInstance<ServiceMeta>> {


    private static final int VIRTUAL_NODE_SIZE = 10;
    private static final String VIRTUAL_NODE_SPLIT = "#";

    /**
     * 基于一致性HASH的负载均衡策略
     * @param servers 服务列表
     * @param hashCode hash值
     * @param sourceIp 源IP地址
     * @return
     */
    @Override
    public ServiceInstance<ServiceMeta> select(List<ServiceInstance<ServiceMeta>> servers, int hashCode, String sourceIp) {

        log.debug("service load balancer by consistency hash...");

        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = this.makeConsistentHashRing(servers);
        return this.allocateNode(ring, hashCode, sourceIp);
    }



    private ServiceInstance<ServiceMeta> allocateNode(TreeMap<Integer, ServiceInstance<ServiceMeta>> ring, int hashCode, String sourceIp) {

        // ceilingEntry 方法放回第一个大于或等于指定key的Entry, 不存在则返回 null
        Map.Entry<Integer, ServiceInstance<ServiceMeta>> instanceEntry = ring.ceilingEntry(hashCode);
        if (Objects.isNull(instanceEntry)) {
            instanceEntry = ring.firstEntry();
        }

        if (Objects.isNull(instanceEntry)) {
            throw new RuntimeException("not discover useful service instance, please register a service instance in registry center!");
        }

        return instanceEntry.getValue();
    }


    private TreeMap<Integer, ServiceInstance<ServiceMeta>> makeConsistentHashRing(List<ServiceInstance<ServiceMeta>> servers) {

        TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = new TreeMap<>();

        for (ServiceInstance<ServiceMeta> server : servers) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                ring.put((this.buildServiceInstanceKey(server) + VIRTUAL_NODE_SPLIT + i).hashCode(), server);
            }
        }
        return ring;
    }



    private String buildServiceInstanceKey(ServiceInstance<ServiceMeta> server) {

        ServiceMeta payload = server.getPayload();
        return String.join(":",payload.getServiceAddress(), String.valueOf(payload.getPort()));
    }


}
