package com.hk.rpc.test.consumer;

import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.consumer.RpcClient;
import com.hk.rpc.proxy.api.async.IAsyncObjectProxy;
import com.hk.rpc.proxy.api.future.RPCFuture;
import com.hk.rpc.test.api.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    public static final String registryAddress = "47.108.146.141:2181";
    public static final String registryType = "zookeeper";

    private RpcClient rpcClient;

    @Before
    public void init() {
        rpcClient = new RpcClient(registryAddress, registryType, "1.0.0", "hk-hub",
                3000, RpcConstants.SERIALIZATION_PROTOBUF, false, false);
    }


    @Test
    public void testSync() {


        // 获取代理对象
        DemoService demoService = rpcClient.create(DemoService.class);

        // 执行rpc 调用
        String res = demoService.hello("测试服务消费者整合动态代理直接调用服务获取返回数据结果!!!");

        log.info("rpc call result={}", res);
        rpcClient.shutdown();
    }


    @Test
    public void testAsync() throws ExecutionException, InterruptedException, TimeoutException {

        // 获取代理对象
        IAsyncObjectProxy proxy = rpcClient.createAsync(DemoService.class);

        // 执行rpc 调用
        RPCFuture future = proxy.call("hello", "async call");

        log.info("async rpc call result={}", future.get(3000, TimeUnit.MILLISECONDS));
        rpcClient.shutdown();
    }



}
