package com.hk.rpc.loadbalance.api.helper;

import com.hk.rpc.protocol.meta.ServiceMeta;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : HK意境
 * @ClassName : ServiceLoadBalancerHelper
 * @date : 2023/6/28 21:19
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ServiceLoadBalancerHelper {

    private static volatile List<ServiceMeta> cachedServiceMetaList = new CopyOnWriteArrayList<>();


    public static List<ServiceMeta> getServiceMetaList(List<ServiceInstance<ServiceMeta>> serviceInstanceList) {

        if (CollectionUtils.isEmpty(serviceInstanceList) || cachedServiceMetaList.size() == serviceInstanceList.size()) {
            return cachedServiceMetaList;
        }

        // 先清空 cachedServiceMetaList 中的数据
        cachedServiceMetaList.clear();

        serviceInstanceList.forEach(instance -> cachedServiceMetaList.add(instance.getPayload()));
        return cachedServiceMetaList;
    }


}
