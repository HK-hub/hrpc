package com.hk.rpc.loadbalance.api.base;

import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.protocol.meta.ServiceMeta;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author : HK意境
 * @ClassName : BaseEnhancedServiceLoadBalancer
 * @date : 2023/6/28 21:09
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public abstract class BaseEnhancedServiceLoadBalancer implements ServiceLoadbalancer<ServiceMeta> {

    /**
     * 根据权重重新生成服务元数据列表，权重越高的元数据，会在最终的服务列表中出现的次数越多
     * @param serviceMetaList 原始服务列表
     * @return 加权后的服务列表
     */
    protected List<ServiceMeta> getWeightServiceMetaList(List<ServiceMeta> serviceMetaList) {

        if (CollectionUtils.isEmpty(serviceMetaList)) {
            return null;
        }

        ArrayList<ServiceMeta> serviceMetaWeightedList = new ArrayList<>();
        serviceMetaList.forEach(server ->
                IntStream.range(0, server.getWeight()).forEach(i -> serviceMetaWeightedList.add(server)));

        return serviceMetaWeightedList;
    }

}
