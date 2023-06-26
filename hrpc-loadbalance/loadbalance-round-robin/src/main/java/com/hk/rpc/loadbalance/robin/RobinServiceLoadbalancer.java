package com.hk.rpc.loadbalance.robin;

import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : HK意境
 * @ClassName : RobinServiceLoadbalancer
 * @date : 2023/6/26 21:45
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class RobinServiceLoadbalancer<T> implements ServiceLoadbalancer<T> {

    private static final AtomicInteger counter = new AtomicInteger(0);

    /**
     * 基于轮询算法实现负载均衡策略
      * @param servers 服务列表
     * @param hashCode hash值
     * @return
     */
    @Override
    public T select(List<T> servers, int hashCode) {

        log.debug("load balance by round robin...");

        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        int size = servers.size();
        int index = counter.incrementAndGet();

        if (index >= Integer.MAX_VALUE - 10000) {
            counter.set(0);
        }

        return servers.get(index % size);
    }
}
