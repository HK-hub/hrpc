package com.hk.rpc.demo.nativ.provider.impl;

import com.hk.rpc.annotation.RpcService;
import com.hk.rpc.demo.api.DemoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "io.binghe.rpc.test.api.DemoService", version = "1.0.0", group = "binghe", weight = 2)
public class ProviderDemoServiceImpl implements DemoService {

    @Override
    public String hello(String name) {
        log.info("调用hello方法传入的参数为===>>>{}", name);
        return "hello " + name;
    }
}
