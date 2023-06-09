package com.hk.rpc.protocol.header;

import com.hk.rpc.common.id.IdFactory;
import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.protocol.enumeration.RpcType;

/**
 * @author : HK意境
 * @ClassName : RpcHeaderFactory
 * @date : 2023/6/9 22:36
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcHeaderFactory {

    /**
     * 构造 Rpc 消息头部
     * @param serializationType 序列化类型
     * @return
     */
    public static RpcHeader getRequestHeader(String serializationType) {

        RpcHeader rpcHeader = new RpcHeader();

        // 设置请求id
        long requestId = IdFactory.getId();
        rpcHeader.setMagic(RpcConstants.MAGIC)
                .setRequestId(requestId)
                .setMsgType((byte) RpcType.REQUEST.getType())
                .setStatus((byte) 0x1)
                .setSerializationType(serializationType);
        return rpcHeader;
    }


}
