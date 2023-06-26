package com.hk.rpc.loadbalance.hash.weight;

import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author : HK意境
 * @ClassName : HashServiceLoadbalancer
 * @date : 2023/6/26 21:45
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class HashWeightServiceLoadbalancer<T> implements ServiceLoadbalancer<T> {


    /**
     * 基于加权HASH算法实现负载均衡策略
      * @param servers 服务列表
     * @param hashCode hash值
     * @return
     */
    @Override
    public T select(List<T> servers, int hashCode) {

        log.debug("load balance by hash weight...");

        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        hashCode = Math.abs(hashCode);
        int count = hashCode % servers.size();
        if (count == 0) {
            count = servers.size();
        }

        return servers.get(hashCode % count);
    }
}
