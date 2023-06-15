package com.hk.rpc.proxy.api.async;

import com.hk.rpc.proxy.api.future.RPCFuture;

/**
 * @author : HK意境
 * @ClassName : IAsyncObjectProxy
 * @date : 2023/6/15 20:56
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public interface IAsyncObjectProxy {

    /**
     * 异步 代理对象调用方法
     * @param methodName 方法名称
     * @param args 方法参数
     * @return
     */
    RPCFuture call(String methodName, Object... args);

}
