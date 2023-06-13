package com.hk.rpc.consumer.common.initializer;

import com.hk.rpc.codec.RpcDecoder;
import com.hk.rpc.codec.RpcEncoder;
import com.hk.rpc.consumer.common.handler.RpcConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

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

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new RpcEncoder())
                .addLast(new RpcDecoder())
                .addLast(new RpcConsumerHandler());
    }
}
