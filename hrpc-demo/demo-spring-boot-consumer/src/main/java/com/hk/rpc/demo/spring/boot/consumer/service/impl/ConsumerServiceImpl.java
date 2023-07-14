package com.hk.rpc.demo.spring.boot.consumer.service.impl;

import com.hk.rpc.annotation.RpcReference;
import com.hk.rpc.demo.api.DemoService;
import com.hk.rpc.demo.spring.boot.consumer.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : HK意境
 * @ClassName : ConsumerServiceImpl
 * @date : 2023/7/14 10:46
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@Service
public class ConsumerServiceImpl implements ConsumerService {

    @RpcReference(registryAddress = "localhost:2181", registryType = "zookeeper", loadBalanceType = "random", version = "1.0.0", group = "hk", timeout = 3000,
    serializationType = "protobuf", proxy = "cglib", retryTimes = 1, retryInterval = 2000)
    private DemoService demoService;


    @Override
    public String hello(String name) {

        String res = this.demoService.hello(name);
        log.info("rpc 调用结果:{}", name);
        return res;
    }
}
