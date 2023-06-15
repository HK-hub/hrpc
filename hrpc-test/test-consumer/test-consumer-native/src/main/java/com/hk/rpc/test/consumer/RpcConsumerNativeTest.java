package com.hk.rpc.test.consumer;

import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.consumer.RpcClient;
import com.hk.rpc.test.api.DemoService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : RpcConsumerNativeTest
 * @date : 2023/6/15 20:34
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RpcConsumerNativeTest {

    public static void main(String[] args) {

        RpcClient rpcClient = new RpcClient("1.0.0", "hk-hub", 3000, RpcConstants.SERIALIZATION_JDK, false, false);

        // 获取代理对象
        DemoService demoService = rpcClient.create(DemoService.class);

        // 执行rpc 调用
        String res = demoService.hello("测试服务消费者整合动态代理直接调用服务获取返回数据结果!!!");

        log.info("rpc call result={}", res);
        rpcClient.shutdown();
    }


}
