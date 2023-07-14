package com.hk.rpc.spring.boot.consumer.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : HK意境
 * @ClassName : SpringBootConsumerConfig
 * @date : 2023/7/14 9:32
 * @description : 服务消费者配置类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class SpringBootConsumerConfig {

    /**
     * 注册中心地址
     */
    private String registryAddress;


    /**
     * 注册中心类型
     */
    private String registryType;


    /**
     * 负载均衡
     */
    private String loadBalanceType;


    /**
     * 代理类型
     */
    private String proxy;


    /**
     * 服务分组
     */
    private String group;

    /**
     * 版本
     */
    private String version;


    /**
     * 序列化类型
     */
    private String serializationType;


    /**
     * 超时时间
     */
    private int timeout;


    /**
     * 是否异步
     */
    private boolean async;

    /**
     * 单向调用
     */
    private boolean oneway;


    /**
     * 心跳检测时间间隔
     */
    private int heartbeatInterval = 30000;


    /**
     * 不活跃连接扫描时间间隔
     */
    private int scanInactiveInterval = 60000;


    /**
     * 重试机制重试次数
     */
    private int retryTimes = 3;


    /**
     * 重试时间间隔
     */
    private int retryInterval = 1000;

}
