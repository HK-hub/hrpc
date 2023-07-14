package com.hk.rpc.spring.boot.provider.starter;

import com.hk.rpc.provider.spring.RpcSpringServer;
import com.hk.rpc.spring.boot.provider.config.SpringBootProviderConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : HK意境
 * @ClassName : SpringBootProviderAutoConfiguration
 * @date : 2023/7/13 21:28
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Configuration
public class SpringBootProviderAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "hrpc.provider")
    public SpringBootProviderConfig springBootProviderConfig() {

        return new SpringBootProviderConfig();
    }


    @Bean
    public RpcSpringServer rpcSpringServer(final SpringBootProviderConfig springBootProviderConfig) {

        return new RpcSpringServer(springBootProviderConfig.getServerAddress(),
                springBootProviderConfig.getRegistryAddress(),
                springBootProviderConfig.getRegistryType(),
                springBootProviderConfig.getLoadBalanceType(),
                springBootProviderConfig.getReflectType(),
                springBootProviderConfig.getHeartbeatInterval(),
                springBootProviderConfig.getScanInactiveChannelInterval());
    }


}
