package com.hk.rpc.consumer.common.initializer;

import com.hk.rpc.codec.RpcDecoder;
import com.hk.rpc.codec.RpcEncoder;
import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.consumer.common.handler.RpcConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author : HK意境
 * @ClassName : RpcConsumerInitializer
 * @date : 2023/6/13 20:40
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 心跳间隔时间
     */
    private int heartbeatInterval;

    public RpcConsumerInitializer(int heartbeatInterval) {

        if (heartbeatInterval > 0) {
            this.heartbeatInterval = heartbeatInterval;
        }
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(RpcConstants.CODEC_ENCODER, new RpcEncoder())
                .addLast(RpcConstants.CODEC_DECODER, new RpcDecoder())
                .addLast(RpcConstants.CODEC_CLIENT_IDLE_HANDLER,
                        new IdleStateHandler(this.heartbeatInterval, 0, 0, TimeUnit.MILLISECONDS))
                .addLast(new RpcConsumerHandler());
    }
}
