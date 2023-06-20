package com.hk.rpc.consumer.common;

import com.hk.rpc.common.helper.RpcServiceHelper;
import com.hk.rpc.common.thread.ClientThreadPool;
import com.hk.rpc.consumer.common.helper.RpcConsumerHandlerHelper;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.proxy.api.consumer.Consumer;
import com.hk.rpc.proxy.api.future.RPCFuture;
import com.hk.rpc.consumer.common.handler.RpcConsumerHandler;
import com.hk.rpc.consumer.common.initializer.RpcConsumerInitializer;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.registry.api.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import java.util.Map;
import java.util.Objects;
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
public class RpcConsumer implements Consumer {

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
        RpcConsumerHandlerHelper.closeRpcClientHandler();
        this.eventLoopGroup.shutdownGracefully();
        ClientThreadPool.shutdown();
    }


    /**
     * 将特定服务消费者与服务提供者之间的连接进行缓存
     * @param protocol
     */
    @Override
    public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {

        // 获取请求配置
        RpcRequest request = protocol.getBody();
        String serviceKey = RpcServiceHelper.locationService(request.getClassName(), request.getVersion(), request.getGroup());

        Object[] parameters = request.getParameters();
        int invokerHashCode = 0;
        if (parameters == null || parameters.length == 0) {
            invokerHashCode = serviceKey.hashCode();
        } else {
            invokerHashCode = parameters[0].hashCode();
        }

        // 服务发现: 负载均衡的进行服务发现
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokerHashCode);
        if (Objects.nonNull(serviceMeta)) {
            RpcConsumerHandler handler = RpcConsumerHandlerHelper.get(serviceMeta);
            // 缓存中无 handler
            if (Objects.isNull(handler)) {
                handler = getRpcConsumerHandler(serviceMeta.getServiceAddress(), serviceMeta.getPort());
                RpcConsumerHandlerHelper.put(serviceMeta, handler);
            } else if (BooleanUtils.isFalse(handler.getChannel().isActive())) {
                // 存在handler 但是不活跃
                handler.close();
                handler = getRpcConsumerHandler(serviceMeta.getServiceAddress(), serviceMeta.getPort());
                RpcConsumerHandlerHelper.put(serviceMeta, handler);
            }

            return handler.sendRequest(protocol, request.isAsync(), request.isOneway());
        }

        return null;
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
