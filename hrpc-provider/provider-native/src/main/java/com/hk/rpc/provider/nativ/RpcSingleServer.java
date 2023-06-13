package com.hk.rpc.provider.nativ;

import com.hk.rpc.common.scanner.server.RpcServiceScanner;
import com.hk.rpc.provider.common.server.base.BaseServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : RpcSingleServer
 * @date : 2023/6/9 14:26
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RpcSingleServer extends BaseServer {

    /**
     * 指定地址，端口
     *
     * @param host
     * @param port
     */
    public RpcSingleServer(String host, int port, String scanPackage) {
        super(host, port);

        // 获取 @RpcService 注解标注的类映射信息
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(host, port, scanPackage);

        } catch (Exception e) {
            log.error("RPC Server init error:", e);
        }
    }

    public RpcSingleServer(String host, int port, String scanPackage, String reflectType) {
        super(host, port, reflectType);

        // 获取 @RpcService 注解标注的类映射信息
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(host, port, scanPackage);

        } catch (Exception e) {
            log.error("RPC Server init error:", e);
        }
    }

}
