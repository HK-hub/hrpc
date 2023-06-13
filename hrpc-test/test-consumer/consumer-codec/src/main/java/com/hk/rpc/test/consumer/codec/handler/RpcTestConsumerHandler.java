package com.hk.rpc.test.consumer.codec.handler;

import com.alibaba.fastjson2.JSONObject;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.header.RpcHeaderFactory;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : RpcTestConsumerHandler
 * @date : 2023/6/10 18:09
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RpcTestConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    /**
     * 处理器活跃就发送 RpcRequest 请求
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.info("发送RPC请求...");

        //模拟发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        RpcRequest request = new RpcRequest();

        request.setClassName("com.hk.rpc.test.api.DemoService");
        request.setGroup("hk-hub");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"testRpcRequest"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);
        protocol.setBody(request);
        log.info("服务消费者发送的数据===>>>{}", JSONObject.toJSONString(protocol));
        ctx.writeAndFlush(protocol);
        log.info("发送数据完毕...");
    }

    /**
     * 监听到响应数据
     * @param ctx
     * @param protocol
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) throws Exception {
        log.info("服务消费者接收到的数据===>>>{}", JSONObject.toJSONString(protocol));
    }
}
