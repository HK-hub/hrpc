package com.hk.rpc.loadbalance.hash;

import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
public class HashServiceLoadbalancer<T> implements ServiceLoadbalancer<T> {


    /**
     * 基于轮询算法实现负载均衡策略
      * @param servers 服务列表
     * @param hashCode hash值
     * @return
     */
    @Override
    public T select(List<T> servers, int hashCode, String sourceIp) {

        log.debug("load balance by hash...");

        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        hashCode = Math.abs(hashCode);
        int index = hashCode % servers.size();

        return servers.get(index);
    }
}
