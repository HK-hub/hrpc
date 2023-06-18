package com.hk.rpc.loadbalance.api;

import java.util.List;

/**
 * @author : HK意境
 * @ClassName : ServiceLoadbalancer
 * @date : 2023/6/17 17:07
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public interface ServiceLoadbalancer<T> {


    /**
     * 以负载均衡的方式选择一个节点
     * @param servers 服务列表
     * @param hashCode hash值
     * @return 可用的服务节点
     */
    T select(List<T> servers, int hashCode);

}