package com.hk.rpc.demo.docker.provider.impl;

import com.hk.rpc.annotation.RpcService;
import com.hk.rpc.demo.api.DemoService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : ProviderDemoServiceImpl
 * @date : 2023/7/14 14:59
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@RpcService(interfaceClass = DemoService.class, version = "1.0", group = "hk", weight = 2)
public class ProviderDemoServiceImpl implements DemoService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
