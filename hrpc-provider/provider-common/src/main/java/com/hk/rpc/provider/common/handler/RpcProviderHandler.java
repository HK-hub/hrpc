package com.hk.rpc.provider.common.handler;

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
public class RpcProviderHandler extends SimpleChannelInboundHandler<Object> {

    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {

        log.info("RPC提供者收到的数据==>>>{}", o.toString());
        log.info("handlerMap中存放的数据如下==>>>{}", handlerMap.toString());

        // 直接返回数据
        ctx.writeAndFlush(o);
    }



}
