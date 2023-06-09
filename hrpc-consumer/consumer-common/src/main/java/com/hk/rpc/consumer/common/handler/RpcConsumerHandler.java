package com.hk.rpc.consumer.common.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.consumer.common.cache.ConsumerChannelCache;
import com.hk.rpc.consumer.common.context.RpcContext;
import com.hk.rpc.consumer.common.manager.ConsumerConnectionManager;
import com.hk.rpc.protocol.enumeration.RpcStatus;
import com.hk.rpc.protocol.enumeration.RpcType;
import com.hk.rpc.protocol.header.RpcHeaderFactory;
import com.hk.rpc.proxy.api.future.RPCFuture;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.header.RpcHeader;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.protocol.response.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : HK意境
 * @ClassName : RpcConsumerHandler
 * @date : 2023/6/13 20:26
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private volatile Channel channel;
    private SocketAddress remotePeer;

    /**
     * 存储请求ID与RpcResponse 的映射关系
     */
    // private Map<Long, RpcProtocol<RpcResponse>> pendingResponseMap = new ConcurrentHashMap<>();

    private Map<Long, RPCFuture> pendingFutureMap = new ConcurrentHashMap<>();


    /**
     * Netty 注册连接时，初始化 channel 字段
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    /**
     * Netty 激活连接时, 获取 channel 属性的 对端
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
        ConsumerChannelCache.addChannel(this.channel);
    }


    /**
     * 读取数据
     * @param ctx
     * @param protocol
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) throws Exception {

        if (Objects.isNull(protocol)) {
            return;
        }

        log.info("服务消费者收到的数据>>>{}", JSON.toJSONString(protocol));
        this.handleMessage(protocol, ctx.channel());
    }


    /**
     * 处理接收到的消息
     * @param protocol
     */
    private void handleMessage(RpcProtocol<RpcResponse> protocol, Channel channel) {

        RpcHeader header = protocol.getHeader();
        int msgType = header.getMsgType();
        if (Objects.equals(msgType, RpcType.HEARTBEAT_TO_CONSUMER.getType())) {
            // 服务提供者响应给服务消费者的心跳消息: pong
            this.handleHeartbeatMessage(protocol);
        } else if (Objects.equals(msgType, RpcType.HEARTBEAT_FROM_PROVIDER.getType())) {
            // 服务消费者发送给服务提供者的心跳消息: ping
            this.handleHeartbeatMessageFromProvider(protocol, channel);
        } else if (Objects.equals(msgType, RpcType.RESPONSE.getType())) {
            // 响应消息
            this.handleResponseMessage(protocol);
        }
    }


    /**
     * 处理请求消息
     * @param protocol
     */
    private void handleResponseMessage(RpcProtocol<RpcResponse> protocol) {

        // 解析响应协议数据
        RpcHeader header = protocol.getHeader();

        RPCFuture rpcFuture = this.pendingFutureMap.remove(header.getRequestId());
        if (Objects.nonNull(rpcFuture)) {
            rpcFuture.done(protocol);
        }
    }


    /**
     * 处理心跳 pong 消息
     * @param protocol
     */
    private void handleHeartbeatMessage(RpcProtocol<RpcResponse> protocol) {

        log.debug("rpc consumer receive provider={} heartbeat message:{}", this.channel.remoteAddress(), protocol.getBody().getResult());
        // 清除失联计数
        ConsumerConnectionManager.cleanMissHeartbeatCounter(this.channel.id().asLongText());
    }


    /**
     * 处理 ping 消息
     * 接收服务提供者发过来的ping 消息，并且响应pong消息
     * @param protocol
     * @param channel
     */
    private void handleHeartbeatMessageFromProvider(RpcProtocol<RpcResponse> protocol, Channel channel) {

        RpcHeader header = protocol.getHeader();
        header.setMsgType((byte) RpcType.HEARTBEAT_TO_PROVIDER.getType());

        // 发送 ping 消息
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
        RpcRequest request = new RpcRequest();
        request.setParameters(new Object[]{RpcConstants.HEARTBEAT_PONG});
        header.setStatus((byte) RpcStatus.SUCCESS.getCode());

        requestRpcProtocol.setHeader(header).setBody(request);
        channel.writeAndFlush(requestRpcProtocol);
    }


    /**
     * 服务消费者向服务提供者发送请求
     */
    public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol, boolean async, boolean oneway){

        log.info("服务消费者发送的数据===>>>{}", JSONObject.toJSONString(protocol));
        channel.writeAndFlush(protocol);

        // 选择对应的调用方式
        if (BooleanUtils.isTrue(oneway)) {
            // 单向调用
            return sendRequestOneway(protocol);
        }

        if (BooleanUtils.isTrue(async)) {
            // 异步调用
            return sendRequestAsync(protocol);
        }

        // 同步调用
        return this.sendRequestSync(protocol);
    }


    /**
     * 同步调用
     * @param protocol
     * @return
     */
    private RPCFuture sendRequestSync(RpcProtocol<RpcRequest> protocol) {
        RPCFuture rpcFuture = this.getRpcFuture(protocol);
        channel.writeAndFlush(protocol);
        return rpcFuture;
    }


    /**
     * 异步调用
     * @param protocol
     * @return
     */
    private RPCFuture sendRequestAsync(RpcProtocol<RpcRequest> protocol) {

        // 异步转同步
        RPCFuture rpcFuture = this.getRpcFuture(protocol);
        // 如果是异步调用，则将RPCFuture 放入 RpcContext 中
        RpcContext.getContext().setRPCFuture(rpcFuture);
        // 发送协议数据
        this.channel.writeAndFlush(protocol);

        return rpcFuture;
    }


    /**
     * 发送单向调用
     * @param protocol
     */
    private RPCFuture sendRequestOneway(RpcProtocol<RpcRequest> protocol) {

        this.channel.writeAndFlush(protocol);
        return null;
    }


    /**
     * 构造RPC 请求 Future
     * @param protocol
     * @return
     */
    private RPCFuture getRpcFuture(RpcProtocol<RpcRequest> protocol) {

        RPCFuture rpcFuture = new RPCFuture(protocol);
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();

        // 放入 pendingMap 中
        this.pendingFutureMap.put(requestId, rpcFuture);

        return rpcFuture;

    }


    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }


    /**
     * 读超时事件
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {

            // 发送一次心跳数据
            RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOBUF, RpcType.HEARTBEAT_FROM_CONSUMER.getType());
            RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
            RpcRequest request = new RpcRequest();
            request.setParameters(new Object[]{RpcConstants.HEARTBEAT_PING});
            requestRpcProtocol.setHeader(header).setBody(request);

            ctx.channel().writeAndFlush(requestRpcProtocol);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
