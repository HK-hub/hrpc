package com.hk.rpc.loadbalance.random.weight;

import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * @author : HK意境
 * @ClassName : RandomWeightServiceLoadBalancer
 * @date : 2023/6/26 21:14
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class RandomWeightServiceLoadBalancer<T> implements ServiceLoadbalancer<T> {


    /**
     * 加权随机算法实现负载均衡
     * @param servers 服务列表
     * @param hashCode hash值
     * @return
     */
    @Override
    public T select(List<T> servers, int hashCode, String sourceIp) {

        log.debug("weight random service load balancer...");

        if (CollectionUtils.isEmpty(servers)) {
            return  null;
        }

        hashCode = Math.abs(hashCode);
        int count = hashCode % servers.size();

        if (count <= 1) {
            count = servers.size();
        }

        Random random = new Random();
        int index = random.nextInt(count);

        return servers.get(index);
    }
}
