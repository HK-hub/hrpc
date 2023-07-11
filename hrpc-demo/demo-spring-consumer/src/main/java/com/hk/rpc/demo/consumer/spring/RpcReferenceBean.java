package com.hk.rpc.demo.consumer.spring;

import com.hk.rpc.consumer.RpcClient;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author : HK意境
 * @ClassName : RpcReferenceBean
 * @date : 2023/7/10 20:40
 * @description : 通过实现FactoryBean 接口来创建Bean对象，并且注入到IOC容器中
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Data
public class RpcReferenceBean<T> implements FactoryBean<T> {

    /**
     * 接口类
     */
    private Class<T> interfaceClass;

    /**
     * 接口类名称
     */
    private String interfaceClassName;


    /**
     * 版本
     */
    private String version;


    /**
     * 服务分组
     */
    private String group;

    /**
     * 注册中心类型
     */
    private String registryType;


    /**
     * 注册中心地址
     */
    private String registryAddress;


    /**
     * 负载均衡策略
     */
    private String loadBalanceType;


    /**
     * 序列化类型
     */
    private String serializationType;


    /**
     * 超时时间，单位:毫秒
     */
    private long timeout;


    /**
     * 异步调用
     */
    private boolean async;


    /**
     * 单向调用
     */
    private boolean oneway;


    /**
     * 代理方式
     */
    private String proxyType;


    /**
     * 生成的代理对象
     */
    private T proxyObject;


    /**
     * 心跳检测时间
     */
    private int heartbeatInterval;

    /**
     * 扫描空闲连接时间间隔
     */
    private int scanInactiveChannelInterval;


    /**
     * 重试次数
     */
    private int retryTimes = 3;


    /**
     * 重试时间间隔
     */
    private int retryInterval = 1000;


    /**
     * 创建RpcClient 对象，并且获取代理bean对象
     */
    public void init() {

        RpcClient rpcClient = new RpcClient(this.registryAddress, this.registryType, this.proxyType, this.version, this.group, this.timeout,
                this.serializationType, this.loadBalanceType, this.heartbeatInterval, this.scanInactiveChannelInterval, this.retryInterval,
                this.retryTimes, this.async, this.oneway);
        this.proxyObject = rpcClient.create(this.interfaceClass);
    }



    @Override
    public T getObject() throws Exception {

        return this.proxyObject;
    }

    @Override
    public Class<T> getObjectType() {

        return this.interfaceClass;
    }

}
