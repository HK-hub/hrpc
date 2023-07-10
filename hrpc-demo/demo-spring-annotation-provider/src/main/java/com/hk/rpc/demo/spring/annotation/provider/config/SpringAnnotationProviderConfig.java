package com.hk.rpc.demo.spring.annotation.provider.config;

import com.hk.rpc.provider.spring.RpcSpringServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author : HK意境
 * @ClassName : SpringAnnotationProviderConfig
 * @date : 2023/7/10 15:04
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Configuration
@ComponentScan("com.hk.rpc.demo")
@PropertySource(value = {"classpath:rpc.properties"})
public class SpringAnnotationProviderConfig {

    @Value("${registry.address}")
    private String registryAddress;

    @Value("${registry.type}")
    private String registryType;

    @Value("${registry.loadbalance}")
    private String registryLoadBalanceType;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private int serverPort;

    @Value("${reflect.type}")
    private String reflectType;

    @Value("${server.heartbeat.interval}")
    private int serverHeartbeatInterval;

    @Value(("${server.heartbeat.scan.interval}"))
    private int scanInactiveInterval;


    @Bean
    public RpcSpringServer getRpcSpringServer() {

        return new RpcSpringServer(this.serverAddress, this.serverPort, this.reflectType, this.registryAddress, this.registryLoadBalanceType, this.registryType, this.serverHeartbeatInterval, this.serverHeartbeatInterval);
    }


}
