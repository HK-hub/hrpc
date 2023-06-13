package com.hk.rpc.test.consumer.handler;

import com.hk.rpc.consumer.common.RpcConsumer;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.header.RpcHeaderFactory;
import com.hk.rpc.protocol.request.RpcRequest;

/**
 * @author : HK意境
 * @ClassName : RpcConsumerHandlerTest
 * @date : 2023/6/13 21:09
 * @description : 测试服务消费者
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcConsumerHandlerTest {

    public static void main(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance();
        consumer.sendRequest(getRpcRequestProtocol());
        Thread.sleep(2000);
        consumer.close();
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocol(){
        //模拟发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        RpcRequest request = new RpcRequest();
        request.setClassName("com.hk.rpc.test.api.DemoService");
        request.setGroup("hk-hub");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"HK意境"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
    }
}
