package com.hk.rpc.demo.nativ.consumer;

import lombok.extern.slf4j.Slf4j;
import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.consumer.RpcClient;
import com.hk.rpc.demo.api.DemoService;
import com.hk.rpc.proxy.api.async.IAsyncObjectProxy;
import com.hk.rpc.proxy.api.future.RPCFuture;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class ConsumerNativeDemo {

    public static final String registryAddress = "47.108.146.141:2181";
    public static final String registryType = "zookeeper";
    private RpcClient rpcClient;

    @Before
    public void initRpcClient(){
        rpcClient = new RpcClient(registryAddress, registryType, RpcConstants.PROXY_JAVASSIST, "1.0.0", "hk-hub",
                3000, RpcConstants.SERIALIZATION_KRYO, RpcConstants.SERVICE_LOAD_BALANCER_RANDOM, 30000, 60000, 1000, 3, false, false);
    }


    @Test
    public void testInterfaceRpc() throws InterruptedException {
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("binghe");
        log.info("返回的结果数据===>>> " + result);
        //rpcClient.shutdown();
        while (true){
            Thread.sleep(1000);
        }
    }

    @Test
    public void testAsyncInterfaceRpc() throws Exception {
        IAsyncObjectProxy demoService = rpcClient.createAsync(DemoService.class);
        RPCFuture future = demoService.call("hello", "binghe");
        log.info("返回的结果数据===>>> " + future.get());
        rpcClient.shutdown();
    }
}
