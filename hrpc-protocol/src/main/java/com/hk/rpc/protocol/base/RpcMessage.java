package com.hk.rpc.protocol.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : HK意境
 * @ClassName : RpcMessage
 * @date : 2023/6/9 21:55
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Data
@Accessors(chain = true)
public class RpcMessage implements Serializable {

    /**
     * 单向发送
     */
    protected boolean oneway;

    /**
     * 单向调用
     */
    protected boolean async;

}
