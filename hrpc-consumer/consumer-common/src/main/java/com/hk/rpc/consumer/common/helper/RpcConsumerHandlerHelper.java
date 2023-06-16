package com.hk.rpc.consumer.common.helper;

import com.hk.rpc.consumer.common.handler.RpcConsumerHandler;
import com.hk.rpc.protocol.meta.ServiceMeta;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : HK意境
 * @ClassName : RpcConsumerHandlerHelper
 * @date : 2023/6/16 20:58
 * @description : 缓存服务消费者处理器类(RpcConsumerHandler)的连接：
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcConsumerHandlerHelper {

    public static Map<String, RpcConsumerHandler> rpcConsumerHandlerMap;

    static  {
        rpcConsumerHandlerMap = new ConcurrentHashMap<>();
    }


    /**
     * 计算 key
     * @param meta
     * @return
     */
    public static String getKey(ServiceMeta meta) {
        return meta.getServiceAddress().concat("_").concat(String.valueOf(meta.getPort()));
    }

    /**
     * 放入缓存
     * @param meta
     * @param handler
     */
    public static void put(ServiceMeta meta, RpcConsumerHandler handler) {
        rpcConsumerHandlerMap.put(getKey(meta), handler);
    }


    public static RpcConsumerHandler get(ServiceMeta meta) {
        return rpcConsumerHandlerMap.get(getKey(meta));
    }


    /**
     * 清除连接
     */
    public static void closeRpcClientHandler() {

        Collection<RpcConsumerHandler> consumerHandlers = rpcConsumerHandlerMap.values();
        for (RpcConsumerHandler handler : consumerHandlers) {
            handler.close();
        }

        rpcConsumerHandlerMap.clear();
    }

}
