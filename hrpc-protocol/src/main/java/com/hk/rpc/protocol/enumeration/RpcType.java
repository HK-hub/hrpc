package com.hk.rpc.protocol.enumeration;

import com.hk.rpc.protocol.base.RpcMessage;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.protocol.response.RpcResponse;
import lombok.Data;
import lombok.Getter;

/**
 * @author : HK意境
 * @ClassName : RpcType
 * @date : 2023/6/9 21:48
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Getter
public enum RpcType {

    // 请求消息
    REQUEST(1, RpcRequest.class),
    // 响应消息
    RESPONSE(2, RpcResponse.class),
    // 心跳消息
    HEARTBEAT(3),
    // 从服务消费者发起心跳数据
    HEARTBEAT_FROM_CONSUMER(4),
    // 服务提供者响应服务消费者心跳数据
    HEARTBEAT_TO_CONSUMER(5),
    // 从服务提供者发起心跳数据
    HEARTBEAT_FROM_PROVIDER(6),
    // 服务消费者响应服务提供者心跳数据
    HEARTBEAT_TO_PROVIDER(7),
    // 控制消息
    CONTROL(4),
    ;


    private final int type;
    private final Class<? extends RpcMessage> clazz;

    RpcType(int type) {
        this.type = type;
        this.clazz = null;
    }

    RpcType(int type, Class<? extends RpcMessage> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    /**
     * 根据 type 获取 Rpc 类型
     * @param type 类型编号
     * @return
     */
    public static RpcType findByType(int type) {
        for (RpcType rpcType : RpcType.values()) {
            if (rpcType.getType() == type) {
                return rpcType;
            }
        }
        return null;
    }

}
