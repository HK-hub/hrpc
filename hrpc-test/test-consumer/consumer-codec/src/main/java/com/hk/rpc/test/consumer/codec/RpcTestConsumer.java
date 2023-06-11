package com.hk.rpc.test.consumer.codec;

import com.hk.rpc.test.consumer.codec.init.RpcTestConsumerInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author : HK意境
 * @ClassName : RpcTestConsumer
 * @date : 2023/6/11 14:19
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcTestConsumer {

    public static void main(String[] args) throws InterruptedException {

        // 创建客户端服务器
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new RpcTestConsumerInitializer());
            bootstrap.connect("127.0.0.1", 27880)
                    .sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Thread.sleep(2000);
            workerGroup.shutdownGracefully();
        }

    }

}
