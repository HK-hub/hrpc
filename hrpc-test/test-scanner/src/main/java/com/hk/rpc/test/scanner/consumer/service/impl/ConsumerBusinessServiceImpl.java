package com.hk.rpc.test.scanner.consumer.service.impl;

import com.hk.rpc.annotation.RpcReference;
import com.hk.rpc.test.scanner.consumer.service.ConsumerBusinessService;
import com.hk.rpc.test.scanner.service.DemoService;

/**
 * @author : HK意境
 * @ClassName : ConsumerBusinessServiceImpl
 * @date : 2023/6/8 22:06
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

    @RpcReference(registryAddress = "127.0.0.1:2181", version = "1.0.0", group = "hk-hub")
    protected DemoService demoService;



}
