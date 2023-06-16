package com.hk.rpc.test.registry;

import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.registry.api.RegistryService;
import com.hk.rpc.registry.api.config.RegistryConfig;
import com.hk.rpc.registry.zookeeper.ZookeeperRegistryService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author : HK意境
 * @ClassName : ZookeeperRegistryTest
 * @date : 2023/6/16 16:24
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ZookeeperRegistryTest {

    private RegistryService registryService;
    private ServiceMeta serviceMeta;

    @Before
    public void init() throws Exception {

        RegistryConfig registryConfig = new RegistryConfig("127.0.0.1:2181", "zookeeper");

        this.registryService = new ZookeeperRegistryService();
        registryService.init(registryConfig);

        this.serviceMeta = new ServiceMeta(ZookeeperRegistryTest.class.getName(), "1.0.0", "hk", "127.0.0.1", 8080);
    }


    @Test
    public void testRegister() throws Exception {
        this.registryService.register(serviceMeta);
    }


    @Test
    public void testDiscovery() throws Exception {
        ServiceMeta discovery = this.registryService.discovery(RegistryService.class.getName(), "hk".hashCode());
    }

    @Test
    public void testUnregister() throws Exception {
        this.registryService.unregister(serviceMeta);
    }


    @Test
    public void testDestroy() throws IOException {
        this.registryService.destroy();
    }

}
