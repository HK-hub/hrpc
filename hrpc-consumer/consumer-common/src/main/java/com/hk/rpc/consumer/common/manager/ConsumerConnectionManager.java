package com.hk.rpc.consumer.common.manager;

import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.consumer.common.cache.ConsumerChannelCache;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.enumeration.RpcType;
import com.hk.rpc.protocol.header.RpcHeader;
import com.hk.rpc.protocol.header.RpcHeaderFactory;
import com.hk.rpc.protocol.request.RpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : HK意境
 * @ClassName : ConsumerConnectionManager
 * @date : 2023/7/4 13:57
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class ConsumerConnectionManager {

    /**
     * 心跳检测计数器
     */
    public static final Map<String, HeartbeatCheckContainer> HEARTBEAT_CHECK_CONTAINER = new ConcurrentHashMap<>();


    /**
     * 扫描channel 中不活跃的连接并移除
     */
    public static void scanInactiveChannel() {

        log.debug("scan inactive channel start...");
        Set<Channel> channelCache = ConsumerChannelCache.getChannelCache();
        if (CollectionUtils.isEmpty(channelCache)) {
            return;
        }

        for (Channel channel : channelCache) {
            if (BooleanUtils.isFalse(channel.isActive()) || BooleanUtils.isFalse(channel.isOpen())) {
                // 不活跃的连接，进行关闭
                ConsumerChannelCache.removeChannel(channel);
                channel.close();
            }
        }
        log.debug("scan inactive channel end...");
    }


    /**
     * 向所有连接发送ping 心跳消息
     */
    public static void broadcastPingMessageFromConsumer() {

        Set<Channel> channelCache = ConsumerChannelCache.getChannelCache();
        if (CollectionUtils.isEmpty(channelCache)) {
            return;
        }

        // 构建心跳消息
        RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
        RpcRequest request = new RpcRequest();
        RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOBUF,
                RpcType.HEARTBEAT_FROM_CONSUMER.getType());
        request.setParameters(new Object[]{RpcConstants.HEARTBEAT_PING});
        rpcProtocol.setHeader(header).setBody(request);

        // 广播发送心跳消息
        for (Channel channel : channelCache) {
            if (BooleanUtils.isTrue(channel.isOpen()) && BooleanUtils.isTrue(channel.isActive())) {
                // 活跃连接
                log.debug("consumer={} send heartbeat={} message to provider={}", channel.localAddress(), RpcConstants.HEARTBEAT_PING, channel.remoteAddress());

                // 发送心跳
                channel.writeAndFlush(rpcProtocol);
                // 计数
                increaseMissHeartbeatCounter(channel.id().asLongText());
            }
        }
    }


    /**
     * 消费者重连服务提供者
     */
    public static void reconnectProvider() {

        Collection<HeartbeatCheckContainer> containers = HEARTBEAT_CHECK_CONTAINER.values();

        for (HeartbeatCheckContainer container : containers) {
            AtomicInteger missCounter = container.getMissCounter();
            Channel channel = container.getChannel();
            if (missCounter.get() >= 3 && channel.isActive() && channel.isOpen()) {
                // 连续失联达到三次, 执行重连
                SocketAddress socketAddress = channel.remoteAddress();
                ChannelFuture future = channel.connect(socketAddress);
                container.setChannel(future.channel());
            }
        }
    }



    /**
     * 清除连续没有响应的计数
     * @param id
     */
    public static void cleanMissHeartbeatCounter(String id) {

        HeartbeatCheckContainer container = HEARTBEAT_CHECK_CONTAINER.get(id);
        container.getMissCounter().set(0);
    }


    /**
     * 增加一次失联计数
     * @param id
     * @return
     */
    public static int increaseMissHeartbeatCounter(String id) {

        HeartbeatCheckContainer container = HEARTBEAT_CHECK_CONTAINER.get(id);
        int counter = container.getMissCounter().incrementAndGet();

        return counter;
    }



    @Getter
    @Setter
    public static class HeartbeatCheckContainer {

        /**
         * channel id
         */
        private String id;

        private Channel channel;

        /**
         * 失联次数统计：连续失联3次则进行重连或断开连接
         */
        private AtomicInteger missCounter = new AtomicInteger(0);

        public HeartbeatCheckContainer(Channel channel) {
            this.channel = channel;
            this.id = channel.id().asLongText();
        }

    }

}
