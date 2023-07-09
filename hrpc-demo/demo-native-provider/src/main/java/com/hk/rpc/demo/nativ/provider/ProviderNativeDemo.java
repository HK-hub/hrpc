package com.hk.rpc.demo.nativ.provider;

import com.hk.rpc.provider.nativ.RpcSingleServer;
import org.junit.Test;

public class ProviderNativeDemo {

    @Test
    public void startRpcSingleServer(){
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1", 27880, "127.0.0.1:2181", "zookeeper", "random","io.binghe.rpc.demo", "asm", 3000, 6000);
        singleServer.start();
    }
}
