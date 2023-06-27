package com.hk.rpc.loadbalance.sourceip.hash.weight;

import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author : HK意境
 * @ClassName : SourceIpHashWeightServiceLoadbalancer
 * @date : 2023/6/26 21:45
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class SourceIpHashWeightServiceLoadbalancer<T> implements ServiceLoadbalancer<T> {


    /**
     * 基于源IP地址的加权hash算法实现负载均衡策略
      * @param servers 服务列表
     * @param hashCode hash值
     * @return
     */
    @Override
    public T select(List<T> servers, int hashCode, String sourceIp) {

        log.debug("load balance by source ip hash weight...");

        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        // 传入的源IP地址为空则默认返回第一个服务实例
        if (StringUtils.isEmpty(sourceIp)) {
            return servers.get(0);
        }

        int count = Math.abs(hashCode) % servers.size();
        if (count == 0) {
            count = servers.size();
        }

        int resultHashCode = Math.abs(hashCode + sourceIp.hashCode());

        return servers.get(resultHashCode % count);
    }
}
