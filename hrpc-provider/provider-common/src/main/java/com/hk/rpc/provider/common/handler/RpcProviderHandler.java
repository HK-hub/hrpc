package com.hk.rpc.provider.common.handler;

import com.alibaba.fastjson2.JSON;
import com.hk.rpc.common.helper.RpcServiceHelper;
import com.hk.rpc.common.thread.ServerThreadPool;
import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.enumeration.RpcStatus;
import com.hk.rpc.protocol.enumeration.RpcType;
import com.hk.rpc.protocol.header.RpcHeader;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.protocol.response.RpcResponse;
import com.hk.rpc.provider.common.cache.ProviderChannelCache;
import com.hk.rpc.provider.common.manager.ProviderConnectionManager;
import com.hk.rpc.reflect.api.ReflectInvoker;
import com.hk.rpc.spi.loader.ExtensionLoader;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.Method;
import java.sql.Blob;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author : HK意境
 * @ClassName : RpcProviderHandler
 * @date : 2023/6/9 0:22
 * @description : 进行消息的收发
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final Map<String, Object> handlerMap;

    private ReflectInvoker reflectInvoker;

    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this(RpcConstants.REFLECT_TYPE_CGLIB, handlerMap);
    }

    public RpcProviderHandler(String reflectType, Map<String, Object> handlerMap) {
        reflectInvoker = ExtensionLoader.getExtension(ReflectInvoker.class, reflectType);
        this.handlerMap = handlerMap;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ProviderChannelCache.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ProviderChannelCache.removeChannel(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        ProviderChannelCache.removeChannel(ctx.channel());
    }


    /**
     * 接收到消息
     *
     * @param ctx
     * @param protocol
     *
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {

        log.info("RPC提供者收到的数据==>>>{}", JSON.toJSONString(protocol));
        log.info("handlerMap中存放的数据如下==>>>{}", handlerMap.toString());

        ServerThreadPool.submit(() -> {
            RpcProtocol<RpcResponse> responseRpcProtocol = this.handleMessage(protocol, ctx.channel());

            // 直接返回数据
            ctx.writeAndFlush(responseRpcProtocol)
                    .addListener((ChannelFutureListener) channelFuture ->
                            log.debug("Send response={} for request:{}", responseRpcProtocol, protocol.getHeader().getRequestId()));
        });

    }

    /**
     * 处理消息分发
     *
     * @param protocol
     *
     * @return
     */
    private RpcProtocol<RpcResponse> handleMessage(RpcProtocol<RpcRequest> protocol, Channel channel) {

        RpcProtocol<RpcResponse> response = null;

        // 解析消息类型
        RpcHeader header = protocol.getHeader();

        int msgType = header.getMsgType();
        if (Objects.equals(msgType, RpcType.HEARTBEAT_FROM_CONSUMER.getType())) {
            // 心跳消息：ping
            response = this.handleHeartbeatMessage(protocol, header);
        } else if (Objects.equals(msgType, RpcType.HEARTBEAT_TO_PROVIDER.getType())) {
            // 心跳消息: pong
            this.handleHeartbeatMessageToProvider(protocol, channel);
        } else if (Objects.equals(msgType, RpcType.REQUEST.getType())) {
            // 请求消息
            response = this.handleRequestMessage(protocol, header);
        }

        log.debug("rpc provider handle request={}, and response={}", protocol, response);
        return response;
    }


    /**
     * 处理请求消息
     *
     * @param protocol
     * @param header
     *
     * @return
     */
    private RpcProtocol<RpcResponse> handleRequestMessage(RpcProtocol<RpcRequest> protocol, RpcHeader header) {

        header.setMsgType((byte) RpcType.RESPONSE.getType());
        RpcRequest request = protocol.getBody();
        // 构造响应消息数据
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
        RpcResponse response = new RpcResponse();

        try {
            // 处理请求
            Object result = handle(request);
            response.setResult(result);
            response.setAsync(request.isAsync())
                    .setOneway(request.isOneway());
            header.setStatus((byte) RpcStatus.SUCCESS.getCode());

        } catch (Throwable e) {
            // 设置错误RPC 响应
            response.setMessage(e.toString())
                    .setSuccess(false)
                    //.setFailure(true)
                    .setError(true);
            header.setStatus((byte) RpcStatus.FAILURE.getCode());

            log.error("RPC Server handle request error: ", e);
        }

        // 设置响应数据
        response.setSuccess(true)
                .setFailure(false);

        return responseRpcProtocol.setHeader(header).setBody(response);
    }


    /**
     * 处理心跳消息
     *
     * @param protocol
     * @param header
     *
     * @return
     */
    private RpcProtocol<RpcResponse> handleHeartbeatMessage(RpcProtocol<RpcRequest> protocol, RpcHeader header) {

        log.debug("RPC Server handle Heartbeat:{}", protocol.toString());

        header.setMsgType((byte) RpcType.HEARTBEAT_TO_CONSUMER.getType());
        RpcRequest heartbeat = protocol.getBody();

        // 构造响应 心跳消息
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
        RpcResponse response = new RpcResponse();
        response.setAsync(heartbeat.isAsync())
                .setOneway(heartbeat.isOneway());
        response.setResult(RpcConstants.HEARTBEAT_PONG)
                .setSuccess(true);
        header.setStatus((byte) RpcStatus.SUCCESS.getCode());

        return responseRpcProtocol.setHeader(header).setBody(response);
    }


    /**
     * 处理服务消费者响应给服务提供者的心跳数据
     *
     * @param protocol
     * @param channel
     */
    public void handleHeartbeatMessageToProvider(RpcProtocol<RpcRequest> protocol, Channel channel) {

        log.debug("receive service consumer={}, heartbeat message={}", channel.remoteAddress(), protocol.getBody().toString());
        // 清除失联计数
        ProviderConnectionManager.cleanMissHeartbeatCounter(channel.id().asLongText());
    }


    /**
     * 处理 RPC 调用请求，执行调用目标方法
     *
     * @param request
     *
     * @return 调用结果返回值 或错误
     */
    private Object handle(RpcRequest request) throws Exception {

        // 获取调用目标方法定位key
        String className = request.getClassName();
        String methodName = request.getMethodName();
        String version = request.getVersion();
        String group = request.getGroup();

        String serviceKey = RpcServiceHelper.locationService(className, version, group);
        // 获取服务实例
        Object serviceBean = this.handlerMap.get(serviceKey);
        if (Objects.isNull(serviceBean)) {
            throw new RuntimeException(String.format("target service not exists: key=%s, class=%s, method=%s", serviceKey, className, methodName));
        }

        // 获取目标方法
        Class<?> serviceClass = serviceBean.getClass();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        // 请求信息日志
        log.debug("rpc service class={}, method={}, parameterTypes={}, parameters={}", serviceClass.getName(), methodName,
                Arrays.toString(parameterTypes), Arrays.toString(parameters));

        // 调用目标方法
        return invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
    }


    /**
     * 调用目标方法
     *
     * @param serviceBean    服务Bean
     * @param serviceClass   服务类
     * @param methodName     目标方法
     * @param parameterTypes 方法参数类型集合
     * @param parameters     方法参数集合
     *
     * @return Object result 反射调用方法的结果
     */
    private Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {

        Object result = null;

        // 确定调用方式
        result = this.reflectInvoker.invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
        log.debug("rpc provider invoked method={}#{}, result={}", serviceClass.getName(), methodName, result);

        return result;
    }


    /**
     * Channel 通道出现 异常处理
     *
     * @param ctx
     * @param cause
     *
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught exception", cause);
        ProviderChannelCache.removeChannel(ctx.channel());
        ctx.close();
    }


    /**
     * 心跳事件发生
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        // 如果是 IdleStateEvent
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();
            try {
                log.debug("triggered idle state event, close the channel={}", channel);
                channel.close();
            } finally {
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }

        super.userEventTriggered(ctx, evt);
    }
}
