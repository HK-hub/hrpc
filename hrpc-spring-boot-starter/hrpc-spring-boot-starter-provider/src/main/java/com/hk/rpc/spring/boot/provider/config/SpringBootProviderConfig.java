package com.hk.rpc.spring.boot.provider.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : SpringBootProviderConfig
 * @date : 2023/7/13 21:23
 * @description : Spring Boot Provider 配置类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpringBootProviderConfig {

    /**
     * 服务地址
     */
    private String serverAddress;


    /**
     * 注册地址
     */
    private String registryAddress;


    /**
     * 注册中心类型
     */
    private String registryType;


    /**
     * 负载均衡策略
     */
    private String loadBalanceType;


    /**
     * 反射类型
     */
    private String reflectType;


    /**
     * 心跳时间间隔
     */
    private int heartbeatInterval;


    /**
     * 扫描不活跃连接时间间隔
     */
    private int scanInactiveChannelInterval;

}
