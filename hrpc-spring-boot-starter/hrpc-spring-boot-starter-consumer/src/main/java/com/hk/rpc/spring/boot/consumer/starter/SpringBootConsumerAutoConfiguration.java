package com.hk.rpc.spring.boot.consumer.starter;

import com.hk.rpc.consumer.RpcClient;
import com.hk.rpc.spring.boot.consumer.config.SpringBootConsumerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : HK意境
 * @ClassName : SpringBootConsumerAutoConfiguration
 * @date : 2023/7/14 10:01
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Configuration
@EnableConfigurationProperties
public class SpringBootConsumerAutoConfiguration {

    @Bean
    @ConfigurationProperties("hrpc.consumer")
    public SpringBootConsumerConfig springBootConsumerConfig() {

        return new SpringBootConsumerConfig();
    }


    @Bean
    public RpcClient rpcClient(SpringBootConsumerConfig config) {

        return new RpcClient(config.getRegistryAddress(), config.getRegistryType(), config.getProxy(), config.getVersion(), config.getGroup(), config.getTimeout(), config.getSerializationType(), config.getLoadBalanceType(),
                 config.getHeartbeatInterval(), config.getScanInactiveInterval(), config.getRetryInterval(), config.getRetryTimes(), config.isAsync(), config.isOneway());
    }


}
