package com.hk.rpc.consumer.common;

import com.hk.rpc.consumer.common.future.RPCFuture;
import com.hk.rpc.consumer.common.handler.RpcConsumerHandler;
import com.hk.rpc.consumer.common.initializer.RpcConsumerInitializer;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.request.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : HK意境
 * @ClassName : RpcConsumer
 * @date : 2023/6/13 20:47
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RpcConsumer {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    private static volatile RpcConsumer instance;

    public static Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();

    private RpcConsumer() {
        this.bootstrap = new Bootstrap();
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer());
    }


    /**
     * 双检查单例
     * @return RpcConsumer
     */
    public static RpcConsumer getInstance() {

        if (instance == null) {
            synchronized (RpcConsumer.class) {
                if (instance == null) {
                    instance = new RpcConsumer();
                }
            }
        }

        return instance;
    }


    /**
     * 关闭服务消费者
     */
    public void close() {
        this.eventLoopGroup.shutdownGracefully();
    }


    /**
     * 将特定服务消费者与服务提供者之间的连接进行缓存
     * @param protocol
     * @throws Exception
     */
    public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception {
        //TODO 暂时写死，后续在引入注册中心时，从注册中心获取
        String serviceAddress = "127.0.0.1";
        int port = 27880;
        String key = serviceAddress.concat("_").concat(String.valueOf(port));
        RpcConsumerHandler handler = handlerMap.get(key);
        //缓存中无RpcClientHandler
        if (handler == null){
            handler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);

        }else if (!handler.getChannel().isActive()){  // 缓存中存在RpcClientHandler，但不活跃
            handler.close();
            handler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        }

        // 获取请求配置
        RpcRequest request = protocol.getBody();

        return handler.sendRequest(protocol, request.isAsync(), request.isOneway());
    }


    /**
     * 创建连接并返回 RpcClientHandler
     * @param serviceAddress
     * @param port
     * @return
     */
    private RpcConsumerHandler getRpcConsumerHandler(String serviceAddress, int port) throws InterruptedException {

        ChannelFuture channelFuture = this.bootstrap.connect(serviceAddress, port).sync();

        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (channelFuture.isSuccess()) {
                log.info("connect rpc server {} on port {} success.", serviceAddress, port);
            } else {
                log.error("connect rpc server {} on port {} failed.", serviceAddress, port);
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });

        return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
    }




}
