package com.hk.rpc.proxy.api.consumer;

import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.proxy.api.future.RPCFuture;
import com.hk.rpc.registry.api.RegistryService;

/**
 * @author : HK意境
 * @ClassName : Consumer
 * @date : 2023/6/14 22:56
 * @description : 封装服务消费者动态代理功能屏蔽构建请求协议对象的细节
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public interface Consumer {

    /**
     * 发送RPC 请求
     * @param protocol
     * @return
     */
    public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception;

}
