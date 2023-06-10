package com.hk.rpc.protocol.map;

import com.hk.rpc.protocol.base.RpcMessage;
import com.hk.rpc.protocol.enumeration.RpcType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : HK意境
 * @ClassName : RpcMessageMap
 * @date : 2023/6/10 16:32
 * @description : RPC 类型映射
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcMessageMap {

    public static final Map<RpcType, Class<? extends RpcMessage>> RPC_TYPE_CLASS_MAP = new HashMap<>();


}
