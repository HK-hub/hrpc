package com.hk.rpc.loadbalance.random;

import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * @author : HK意境
 * @ClassName : RandomServiceLoadbalancer
 * @date : 2023/6/17 17:12
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RandomServiceLoadbalancer<T> implements ServiceLoadbalancer<T> {

    /**
     * 随机算法实现负载均衡选择服务
     * @param servers 服务列表
     * @param hashCode hash值
     * @return
     */
    @Override
    public T select(List<T> servers, int hashCode) {

        log.debug("select one server with random load balancer");
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        Random random = new Random();
        int index = random.nextInt(servers.size());

        return servers.get(index);
    }
}
