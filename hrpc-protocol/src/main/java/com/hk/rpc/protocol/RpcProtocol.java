package com.hk.rpc.protocol;

import com.hk.rpc.protocol.header.RpcHeader;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : HK意境
 * @ClassName : RpcProtocol
 * @date : 2023/6/9 23:05
 * @description : Rpc 协议类封装
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Data
@Accessors(chain = true)
public class RpcProtocol<T> implements Serializable {

    private static final long serialVersionUID = 292789485166173277L;

    /**
     * 消息头
     */
    private RpcHeader header;

    /**
     * 消息体
     */
    private T body;


}
