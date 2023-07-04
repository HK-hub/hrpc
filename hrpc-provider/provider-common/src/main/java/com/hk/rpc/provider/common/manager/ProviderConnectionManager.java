package com.hk.rpc.provider.common.manager;

import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.enumeration.RpcType;
import com.hk.rpc.protocol.header.RpcHeader;
import com.hk.rpc.protocol.header.RpcHeaderFactory;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.provider.common.cache.ProviderChannelCache;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Set;

/**
 * @author : HK意境
 * @ClassName : ProviderConnectionManager
 * @date : 2023/7/4 19:34
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class ProviderConnectionManager {

    /**
     * 扫描channel 中不活跃的连接并移除
     */
    public static void scanInactiveChannel() {

        log.debug("scan inactive channel start...");
        Set<Channel> channelCache = ProviderChannelCache.getChannelCache();
        if (CollectionUtils.isEmpty(channelCache)) {
            return;
        }

        for (Channel channel : channelCache) {
            if (BooleanUtils.isFalse(channel.isActive()) || BooleanUtils.isFalse(channel.isOpen())) {
                // 不活跃的连接，进行关闭
                ProviderChannelCache.removeChannel(channel);
                channel.close();
            }
        }
        log.debug("scan inactive channel end...");
    }


    /**
     * 向所有连接发送ping 心跳消息
     */
    public static void broadcastPingMessageFromProvider() {

        Set<Channel> channelCache = ProviderChannelCache.getChannelCache();
        if (CollectionUtils.isEmpty(channelCache)) {
            return;
        }

        // 构建心跳消息
        RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
        RpcRequest request = new RpcRequest();
        RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOBUF, RpcType.HEARTBEAT_FROM_PROVIDER.getType());
        request.setParameters(new Object[]{RpcConstants.HEARTBEAT_PING});
        rpcProtocol.setHeader(header).setBody(request);

        // 广播发送心跳消息
        for (Channel channel : channelCache) {
            if (BooleanUtils.isTrue(channel.isOpen()) && BooleanUtils.isTrue(channel.isActive())) {
                // 活跃连接
                log.debug("consumer={} send heartbeat={} message to provider={}", channel.localAddress(), RpcConstants.HEARTBEAT_PING, channel.remoteAddress());
                channel.writeAndFlush(rpcProtocol);
            }
        }
    }


}
