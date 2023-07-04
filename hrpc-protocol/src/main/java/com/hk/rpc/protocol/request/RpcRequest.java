package com.hk.rpc.protocol.request;

import com.hk.rpc.protocol.base.RpcMessage;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author : HK意境
 * @ClassName : RpcRequest
 * @date : 2023/6/9 22:08
 * @description : RPC 请求 封装类，对应的请求id 在消息头中
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class RpcRequest extends RpcMessage {

    private static final long serialVersionUID = 5555776886650396129L;

    /**
     * 类名称
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;


    /**
     * 参数类型数组
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数数组
     */
    private Object[] parameters;


    /**
     * 版本号
     */
    private String version;

    /**
     * 服务分组
     */
    private String group;
}
