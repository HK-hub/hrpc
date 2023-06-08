package com.hk.rpc.test.scanner.provider;

import com.hk.rpc.annotation.RpcService;
import com.hk.rpc.test.scanner.service.DemoService;

/**
 * @author : HK意境
 * @ClassName : ProviderDemoServiceImpl
 * @date : 2023/6/8 21:50
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.hk.rpc.test.scanner.service.DemoService",
        version = "1.0.0", group = "hk-hub")
public class ProviderDemoServiceImpl implements DemoService {



}
