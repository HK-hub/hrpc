package com.hk.rpc.provider.common.server.base;

import com.hk.rpc.codec.RpcDecoder;
import com.hk.rpc.codec.RpcEncoder;
import com.hk.rpc.provider.common.handler.RpcProviderHandler;
import com.hk.rpc.provider.common.manager.ProviderConnectionManager;
import com.hk.rpc.provider.common.server.api.Server;
import com.hk.rpc.registry.api.RegistryService;
import com.hk.rpc.registry.api.config.RegistryConfig;
import com.hk.rpc.registry.zookeeper.ZookeeperRegistryService;
import com.hk.rpc.spi.loader.ExtensionLoader;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author : HK意境
 * @ClassName : BaseServer
 * @date : 2023/6/9 13:59
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@Data
@Accessors(chain = true)
public class BaseServer implements Server {

    /**
     * 主机域名或 IP地址
     */
    protected String address = "127.0.0.1";

    /**
     * 端口
     */
    protected int port = 27110;

    /**
     * 服务注册中心
     */
    protected RegistryService registryService;

    /**
     * 存储实体类关系
     */
    protected Map<String, Object> handlerMap = new HashMap<>();

    /**
     * 调用方法反射类型
     */
    protected String reflectType;

    /**
     * 心跳定时任务线程池
     */
    protected ScheduledExecutorService executorService;

    /**
     * 心跳间隔时间：30s
     */
    protected int heartbeatInterval = 30000;

    /**
     * 扫描不活跃连接间隔时间：60s
     */
    protected int scanInactiveInterval = 60000;



    
    
    
    /**
     * 指定地址，端口
     * @param address
     * @param port
     */
    public BaseServer(String address, int port, String reflectType, String registryAddress, String registryLoadBalanceType,
                      String registryType, int heartbeatInterval, int scanInactiveInterval) {

        if (StringUtils.isNotEmpty(address)) {
            this.address = address;
        }

        if (port != 0) {
            // 非默认零值
            this.port = port;
        }

        this.reflectType = reflectType;

        // 心跳配置时间
        if (heartbeatInterval > 0) {
            this.heartbeatInterval = heartbeatInterval;
        }

        if (scanInactiveInterval > 0) {
            this.scanInactiveInterval = scanInactiveInterval;
        }

        // registry service
        this.registryService = this.createRegistryService(registryAddress, registryType, registryLoadBalanceType);
    }

    /**
     * 创建服务注册与发现的实现类
     * @param registryAddress
     * @param registryType
     * @param registryLoadBalanceType
     * @return
     */
    private RegistryService createRegistryService(String registryAddress, String registryType, String registryLoadBalanceType) {

        // TODO 后续扩展支持SPI
        RegistryService registryService = null;

        try {
            registryService = ExtensionLoader.getExtension(RegistryService.class, registryType);
            registryService.init(new RegistryConfig(registryAddress, registryType, registryLoadBalanceType));
        } catch (Exception e) {

            log.error("create registry service failed:", e);
        }

        return registryService;
    }


    /**
     * 启动 netty 服务器
     */
    @Override
    public void start() {

        // 定义线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            // 创建服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    // TODO 预留编解码，需要实现自定义协议
                                    .addLast(new RpcDecoder())
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcProviderHandler(reflectType, handlerMap));
                        }
                    })
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定ip, 端口
            ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(this.address, this.port))
                    .sync();
            log.info("Server started on-->>> {}:{}",this.address, this.port);
            // 开始心跳
            this.startHeartbeat();
            future.channel().closeFuture().sync();

        }catch(Exception e){
            log.error("RPC Server start error:", e);

        }finally {
            // 优雅关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    /**
     * 开始心跳检测定时任务
     */
    public void startHeartbeat() {

        this.executorService = Executors.newScheduledThreadPool(2);

        this.executorService.scheduleAtFixedRate(ProviderConnectionManager::broadcastPingMessageFromProvider,
                3, this.heartbeatInterval, TimeUnit.MILLISECONDS);

        this.executorService.scheduleAtFixedRate(ProviderConnectionManager::scanInactiveChannel,
                10, this.scanInactiveInterval,  TimeUnit.MILLISECONDS);
    }


}
