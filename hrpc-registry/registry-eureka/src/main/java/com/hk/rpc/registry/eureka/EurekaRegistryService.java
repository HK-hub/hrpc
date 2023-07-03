package com.hk.rpc.registry.eureka;

import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.registry.api.RegistryService;
import com.hk.rpc.registry.api.config.RegistryConfig;
import com.hk.rpc.spi.annotation.SPIClass;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author : HK意境
 * @ClassName : EurekaRegistryService
 * @date : 2023/7/3 17:28
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class EurekaRegistryService implements RegistryService {

    private EurekaHttpClient client;

    @Override
    public void init(RegistryConfig registryConfig) throws Exception {



    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public void unregister(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIp) throws Exception {
        return null;
    }

    @Override
    public void destroy() throws IOException {

    }
}
