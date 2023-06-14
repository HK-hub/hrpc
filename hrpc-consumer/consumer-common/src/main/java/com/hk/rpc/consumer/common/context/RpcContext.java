package com.hk.rpc.consumer.common.context;

import com.hk.rpc.consumer.common.future.RPCFuture;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : HK意境
 * @ClassName : RpcContext
 * @date : 2023/6/14 15:33
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Data
@Accessors(chain = true)
public class RpcContext {

    /**
     * RpcContext 实例
     */
    private static final RpcContext AGENT = new RpcContext();

    /**
     * 存放 RPCFuture 的 InheritableThreadLocal
     */
    private static final InheritableThreadLocal<RPCFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();


    /**
     * 获取上下文
     * @return RPC服务的上下文信息
     */
    public static RpcContext getContext(){
        return AGENT;
    }

    /**
     * 将RPCFuture 保存到线程的上下文
     * @param rpcFuture
     */
    public void setRPCFuture(RPCFuture rpcFuture) {

        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
    }


    /**
     * 获取RPCFuture
     * @return {@link RPCFuture}
     */
    public RPCFuture getRPCFuture(){
        return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
    }


    /**
     * 移除 RPCFuture
     */
    public void removeRPCFuture() {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
    }

}
