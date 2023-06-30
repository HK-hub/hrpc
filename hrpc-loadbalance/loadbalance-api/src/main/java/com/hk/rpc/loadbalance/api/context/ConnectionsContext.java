package com.hk.rpc.loadbalance.api.context;

import com.hk.rpc.protocol.meta.ServiceMeta;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : HK意境
 * @ClassName : ConnectionsContext
 * @date : 2023/6/30 14:20
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ConnectionsContext {

    private static volatile Map<String, Integer> connectionMap = new ConcurrentHashMap<>();

    public static void add(ServiceMeta serviceMeta) {

        String key = generateKey(serviceMeta);
        Integer count = connectionMap.get(key);

        if (Objects.isNull(count)) {
            count = 0;
        }

        count++;
        connectionMap.put(key, count);
    }


    public static Integer getConnections(ServiceMeta serviceMeta) {

        String key = generateKey(serviceMeta);
        return connectionMap.get(key);
    }


    private static String generateKey(ServiceMeta serviceMeta) {
        return serviceMeta.getServiceAddress().concat(":").concat(String.valueOf(serviceMeta.getPort()));
    }

}
