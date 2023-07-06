package com.hk.rpc.consumer.common.cache;

import com.hk.rpc.consumer.common.manager.ConsumerConnectionManager;
import io.netty.channel.Channel;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author : HK意境
 * @ClassName : ConsumerChannelCache
 * @date : 2023/7/4 13:54
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ConsumerChannelCache {

    private static volatile Set<Channel> channelCache = new CopyOnWriteArraySet<>();

    public static void addChannel(Channel channel) {

        channelCache.add(channel);
        ConsumerConnectionManager.HEARTBEAT_CHECK_CONTAINER.put(channel.id().asLongText(),
                new ConsumerConnectionManager.HeartbeatCheckContainer(channel));
    }

    public static void removeChannel(Channel channel) {

        channelCache.remove(channel);
        ConsumerConnectionManager.HEARTBEAT_CHECK_CONTAINER.remove(channel.id().asLongText());
    }

    public static Set<Channel> getChannelCache() {
        return channelCache;
    }

}
