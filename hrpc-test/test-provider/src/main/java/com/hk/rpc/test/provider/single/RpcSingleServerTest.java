package com.hk.rpc.test.provider.single;

import com.hk.rpc.provider.nativ.RpcSingleServer;
import org.junit.Test;

/**
 * @author : HK意境
 * @ClassName : RpcSingleServerTest
 * @date : 2023/6/9 15:41
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcSingleServerTest {

    @Test
    public void testStartSingleServer() {

        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1",
                27880, "com.hk.rpc.test.provider");
        singleServer.start();

    }


}