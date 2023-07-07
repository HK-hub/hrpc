package com.hk.rpc.test.consumer.handler;

import com.hk.rpc.consumer.common.RpcConsumer;
import com.hk.rpc.consumer.common.context.RpcContext;
import com.hk.rpc.proxy.api.future.RPCFuture;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.header.RpcHeaderFactory;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.proxy.api.callback.DefaultAsyncRPCCallback;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class RpcConsumerHandlerTest {



    public static void main(String[] args) throws Exception {
        mainCallback(args);
    }


    public static void mainCallback(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance(3000, 6000, 1000, 3);
        RPCFuture future = consumer.sendRequest(getRpcRequestProtocol(), null);
        // 添加回调方法
        future.addCallback(new DefaultAsyncRPCCallback() {
            @Override
            public void onSuccess(Object result) {
                log.info("从服务消费者获取到的数据：{}", result);
            }

            @Override
            public void onFailure(Object result) {
                log.info("RPC调用失败：{}", result);
            }

            @Override
            public void onCompleted(Object message) {
                log.info("RPC 调用已经完成了，收到消息如下：{}", message);
            }

            @Override
            public void onException(Exception e) {
                log.info("出现异常：", e);
            }
        });
        // 获取 RPC 调用结果
        Thread.sleep(2000);
        // log.info("从服务消费者获取到的数据:{}", future.get());
        consumer.close();
    }


    public static void mainAsync(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance(3000, 6000, 1000, 3);
        // consumer.sendRequest(getRpcRequestProtocolAsync());
        RPCFuture future = RpcContext.getContext().getRPCFuture();
        log.info("从服务消费者获取到的数据===>>>" + future.get());
        consumer.close();
    }

    public static void mainSync(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance(3000, 6000, 1000, 3);
        RPCFuture future = consumer.sendRequest(getRpcRequestProtocolSync(), null);
        log.info("从服务消费者获取到的数据===>>>" + future.get());
        consumer.close();
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocolOneway(){
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
        request.setOneway(true);
        protocol.setBody(request);
        return protocol;
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocolAsync(){
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
        request.setAsync(true);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocolSync(){
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
        request.setAsync(true);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
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
