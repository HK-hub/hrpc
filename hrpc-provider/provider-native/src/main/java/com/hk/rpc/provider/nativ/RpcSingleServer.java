package com.hk.rpc.provider.nativ;

import com.hk.rpc.provider.common.scanner.RpcServiceScanner;
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
     * 构建服务
     * @param host
     * @param port
     * @param scanPackage
     * @param reflectType
     * @param registryAddress 注册中心地址
     * @param registryType
     */
    public RpcSingleServer(String host, int port, String scanPackage, String reflectType, String loadBalanceType,
                           String registryAddress, String registryType) {
        super(host, port, reflectType, registryAddress, loadBalanceType, registryType);

        // 获取 @RpcService 注解标注的类映射信息
        try {

            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(host, port, scanPackage, this.registryService);
        } catch (Exception e) {
            log.error("RPC Server init error:", e);
        }
        log.info("start rpc server success with address={}, port={}, register={} of {}", host, port, registryAddress, registryType);
    }

}
