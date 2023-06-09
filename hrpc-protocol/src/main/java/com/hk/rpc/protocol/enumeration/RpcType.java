package com.hk.rpc.protocol.enumeration;

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
    REQUEST(1),
    // 响应消息
    RESPONSE(2),
    // 心跳消息
    HEARTBEAT(3),
    // 控制消息
    CONTROL(4),
    ;


    private final int type;

    RpcType(int type) {
        this.type = type;
    }


    /**
     * 根据 type 获取 Rpc 类型
     * @param type
     * @return
     */
    public RpcType findByType(int type) {
        for (RpcType rpcType : RpcType.values()) {
            if (rpcType.getType() == type) {
                return rpcType;
            }
        }
        return null;

    }

}
