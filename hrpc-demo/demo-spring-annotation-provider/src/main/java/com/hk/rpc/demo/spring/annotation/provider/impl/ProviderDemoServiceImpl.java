package com.hk.rpc.demo.spring.annotation.provider.impl;

import com.hk.rpc.annotation.RpcService;
import com.hk.rpc.demo.api.DemoService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : ProviderDemoServiceImpl
 * @date : 2023/7/10 14:56
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.hk.rpc.demo.api.DemoService", version = "1.0", group = "hk", weight = 10)
public class ProviderDemoServiceImpl implements DemoService {
    @Override
    public String hello(String name) {

        log.info("调用hello方法传入的参数为===>>>{}", name);
        return "Hello " + name;
    }
}
