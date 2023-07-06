package com.hk.rpc.provider.common.cache;

import com.hk.rpc.provider.common.manager.ProviderConnectionManager;
import io.netty.channel.Channel;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author : HK意境
 * @ClassName : ProviderChannelCache
 * @date : 2023/7/4 19:33
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ProviderChannelCache {

    private static volatile Set<Channel> channelCache = new CopyOnWriteArraySet<>();

    public static void addChannel(Channel channel) {
        channelCache.add(channel);
        ProviderConnectionManager.HEARTBEAT_CHECK_CONTAINER.put(channel.id().asLongText(),
                new ProviderConnectionManager.HeartbeatCheckContainer(channel));
    }

    public static void removeChannel(Channel channel) {
        channelCache.remove(channel);
        ProviderConnectionManager.HEARTBEAT_CHECK_CONTAINER.remove(channel.id().asLongText());
    }

    public static Set<Channel> getChannelCache() {
        return channelCache;
    }


}
