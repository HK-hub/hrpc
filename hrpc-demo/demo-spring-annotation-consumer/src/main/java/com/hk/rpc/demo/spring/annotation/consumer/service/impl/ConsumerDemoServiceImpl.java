package com.hk.rpc.demo.spring.annotation.consumer.service.impl;

import com.hk.rpc.annotation.RpcReference;
import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.demo.api.DemoService;
import com.hk.rpc.demo.spring.annotation.consumer.service.ConsumerDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : HK意境
 * @ClassName : ConsumerDemoServiceImpl
 * @date : 2023/7/12 21:36
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Service
@Slf4j
public class ConsumerDemoServiceImpl implements ConsumerDemoService {

    @RpcReference(registryType = "zookeeper", registryAddress = "127.0.0.1:2181", loadBalanceType = RpcConstants.SERVICE_LOAD_BALANCER_RANDOM, version = "1.0.0", group = "hk",
            serializationType = RpcConstants.SERIALIZATION_PROTOBUF, proxy = RpcConstants.PROXY_CGLIB, timeout = 3000, asysnc = false, oneway = false)
    private DemoService demoService;


    @Override
    public String hello(String name) {

        log.info("通过接入Spring 注解方式整合服务消费者, 调用hello方法，参数={}", name);
        return demoService.hello(name);
    }
}
