package com.hk.rpc.provider.common.handler;

import com.alibaba.fastjson2.JSON;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.enumeration.RpcType;
import com.hk.rpc.protocol.header.RpcHeader;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author : HK意境
 * @ClassName : RpcProviderHandler
 * @date : 2023/6/9 0:22
 * @description : 进行消息的收发
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }


    /**
     * 接收到消息
     * @param ctx
     * @param protocol
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {

        log.info("RPC提供者收到的数据==>>>{}", JSON.toJSONString(protocol));
        log.info("handlerMap中存放的数据如下==>>>{}", handlerMap.toString());

        // 解析RPC 消息
        RpcHeader header = protocol.getHeader();
        RpcRequest request = protocol.getBody();

        // 将header 中的消息类型设置为响应类型的消息
        header.setMsgType((byte) RpcType.RESPONSE.getType());
        // 构造响应消息数据
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
        RpcResponse response = new RpcResponse();
        response.setMessage("test rpc request and response")
                .setSuccess(true)
                .setResult("数据交互成功")
                .setAsync(request.isAsync())
                .setOneway(request.isOneway());

        responseRpcProtocol.setHeader(header)
                .setBody(response);

        // 直接返回数据
        ctx.writeAndFlush(responseRpcProtocol);

    }
}
