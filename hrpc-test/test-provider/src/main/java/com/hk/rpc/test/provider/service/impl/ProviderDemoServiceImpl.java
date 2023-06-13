package com.hk.rpc.test.provider.service.impl;

import com.hk.rpc.annotation.RpcService;
import com.hk.rpc.test.api.DemoService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : ProviderDemoServiceImpl
 * @date : 2023/6/9 15:01
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.hk.rpc.test.api.DemoService", version = "1.0.0", group = "hk-hub")
public class ProviderDemoServiceImpl implements DemoService {


    @Override
    public String hello(String name) {
        log.info("调用hello 方法传入参数为:{}", name);
        return "hello hrpc!, I am " + name;
    }
}
