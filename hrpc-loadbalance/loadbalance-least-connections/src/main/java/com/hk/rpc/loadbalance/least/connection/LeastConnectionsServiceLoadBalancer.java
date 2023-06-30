package com.hk.rpc.loadbalance.least.connection;

import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.loadbalance.api.context.ConnectionsContext;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : HK意境
 * @ClassName : LeastConnectionsServiceLoadBalancer
 * @date : 2023/6/30 14:33
 * @description : 最少连接数负载均衡
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class LeastConnectionsServiceLoadBalancer implements ServiceLoadbalancer<ServiceMeta> {


    /**
     * 最少连接数负载均衡
     * @param servers 服务列表
     * @param hashCode hash值
     * @param sourceIp 源IP地址
     * @return ServiceMeta
     */
    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {

        log.debug("service consumer load balance by least connections...");

        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        // 获取没有连接的服务列表
        ServiceMeta serviceMeta = this.getEmptyConnectionService(servers);
        if (Objects.isNull(serviceMeta)) {
            // 不存在没有连接的服务列表，获取最小连接服列表
            serviceMeta = this.getLeastConnectionService(servers);
        }
        return serviceMeta;
    }


    /**
     * 获取最少连接数的服务
     * @param servers
     * @return
     */
    private ServiceMeta getLeastConnectionService(List<ServiceMeta> servers) {

        Optional<ServiceMeta> metaOptional = servers.stream()
                .min(Comparator.comparingInt(ConnectionsContext::getConnections));

        return metaOptional.get();
    }


    /**
     * 获取没有连接的服务，服务元数据中连接数为空的服务
     * @param servers
     * @return
     */
    private ServiceMeta getEmptyConnectionService(List<ServiceMeta> servers) {

        for (ServiceMeta server : servers) {
            Integer connections = ConnectionsContext.getConnections(server);
            if (Objects.isNull(connections)) {
                return server;
            }
        }

        return null;
    }
}
