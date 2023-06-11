package com.hk.rpc.test.consumer.codec.init;

import com.hk.rpc.codec.RpcDecoder;
import com.hk.rpc.codec.RpcEncoder;
import com.hk.rpc.test.consumer.codec.handler.RpcTestConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author : HK意境
 * @ClassName : RpcTestConsumerInitializer
 * @date : 2023/6/11 12:30
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcTestConsumerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new RpcEncoder())
                .addLast(new RpcDecoder())
                .addLast(new RpcTestConsumerHandler());


    }
}
