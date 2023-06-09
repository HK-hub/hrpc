package com.hk.rpc.test.provider.service.impl;

import com.hk.rpc.annotation.RpcService;
import com.hk.rpc.test.provider.service.DemoService;

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
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.hk.rpc.test.provider.service.DemoService", version = "1.0", group = "hk-hub")
public class ProviderDemoServiceImpl implements DemoService{
}
