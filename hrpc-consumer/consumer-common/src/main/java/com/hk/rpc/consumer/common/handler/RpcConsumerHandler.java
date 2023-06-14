package com.hk.rpc.consumer.common.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hk.rpc.consumer.common.future.RPCFuture;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.header.RpcHeader;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.protocol.response.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

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

        // 解析响应协议数据
        RpcHeader header = protocol.getHeader();

        RPCFuture rpcFuture = this.pendingFutureMap.remove(header.getRequestId());
        if (Objects.nonNull(rpcFuture)) {
            rpcFuture.done(protocol);
        }
    }


    /**
     * 服务消费者向服务提供者发送请求
     */
    public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol){
        log.info("服务消费者发送的数据===>>>{}", JSONObject.toJSONString(protocol));
        channel.writeAndFlush(protocol);

        // 异步转同步
        RPCFuture rpcFuture = this.getRpcFuture(protocol);
        // 发送协议数据
        this.channel.writeAndFlush(protocol);

        return rpcFuture;
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


}
