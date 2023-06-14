package com.hk.rpc.protocol.response;

import com.hk.rpc.protocol.base.RpcMessage;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : HK意境
 * @ClassName : RpcResponse
 * @date : 2023/6/9 22:13
 * @description : RPC 响应类，对应请求id 在响应头中
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Data
@Accessors(chain = true)
public class RpcResponse extends RpcMessage {

    private static final long serialVersionUID = 425335064405584525L;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 出现异常错误
     */
    private boolean error;

    /**
     * 是否失败
     */
    private boolean failure;

    /**
     * rpc调用响应码
     */
    private int code;


    /**
     * RPC 调用响应结果
     */
    private Object result;

}
