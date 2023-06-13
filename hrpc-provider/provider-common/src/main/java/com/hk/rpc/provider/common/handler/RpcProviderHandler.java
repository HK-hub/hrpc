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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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

    // 调用采用哪种类型调用真实方法
    private final String reflectType;

    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this(RpcConstants.REFLECT_TYPE_CGLIB, handlerMap);
    }

    public RpcProviderHandler(String reflectType, Map<String, Object> handlerMap) {
        this.reflectType = reflectType;
        this.handlerMap = handlerMap;
    }


    /**
     * 接收到消息
     * @param ctx
     * @param protocol
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {

        log.info("RPC提供者收到的数据==>>>{}", JSON.toJSONString(protocol));
        log.info("handlerMap中存放的数据如下==>>>{}", handlerMap.toString());

        ServerThreadPool.submit(() -> {
            // 解析RPC 消息
            RpcHeader header = protocol.getHeader();
            RpcRequest request = protocol.getBody();

            // 将header 中的消息类型设置为响应类型的消息
            header.setMsgType((byte) RpcType.RESPONSE.getType());
            log.debug("Receive request:{}", JSON.toJSONString(request));

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
                response.setMessage(e.toString());
                header.setStatus((byte) RpcStatus.FAILURE.getCode());

                log.error("RPC Server handle request error: ", e);
            }

            // 设置响应数据
            responseRpcProtocol.setHeader(header)
                    .setBody(response);

            // 直接返回数据
            ctx.writeAndFlush(responseRpcProtocol)
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            log.debug("Send response for request:{}", header.getRequestId());
                        }
                    });
        });

    }


    /**
     * 处理 RPC 调用请求，执行调用目标方法
     * @param request
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
     * @param serviceBean 服务Bean
     * @param serviceClass 服务类
     * @param methodName 目标方法
     * @param parameterTypes 方法参数类型集合
     * @param parameters 方法参数集合
     * @return Object result 反射调用方法的结果
     */
    private Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {

        Object result = null;
        // 确定调用方式
        switch (this.reflectType) {
            case RpcConstants.REFLECT_TYPE_JDK:
                result = this.invokeMethodByJdk(serviceBean, serviceClass, methodName, parameterTypes, parameters);
                break;
            case RpcConstants.REFLECT_TYPE_CGLIB:
                result = this.invokeMethodByCglib(serviceBean, serviceClass, methodName, parameterTypes, parameters);
                break;
            default:
                throw new IllegalArgumentException("Unsupported reflection type");
        }

        return result;
    }


    /**
     * cglib 调用方法
     * @param serviceBean 服务Bean
     * @param serviceClass 服务类
     * @param methodName 目标方法
     * @param parameterTypes 方法参数类型集合
     * @param parameters 方法参数集合
     * @return Object result 反射调用方法的结果
     */
    private Object invokeMethodByCglib(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {
        log.info("use cglib reflect type invoke method...");

        // 获取方法
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod method = serviceFastClass.getMethod(methodName, parameterTypes);

        // 执行方法
        return method.invoke(serviceBean, parameters);
    }


    /**
     * JDK 反射调用方法
     * @param serviceBean 服务Bean
     * @param serviceClass 服务类
     * @param methodName 目标方法
     * @param parameterTypes 方法参数类型集合
     * @param parameters 方法参数集合
     * @return Object result 反射调用方法的结果
     */
    private Object invokeMethodByJdk(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {

        log.info("use jdk reflect type invoke method...");
        // 获取目标方法
        Method method = serviceClass.getMethod(methodName, parameterTypes);

        // 设置访问权限
        method.setAccessible(true);

        // 调用方法
        Object result = method.invoke(serviceBean, parameters);

        return result;
    }





    /**
     * Channel 通道出现 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught exception", cause);
        ctx.close();
    }


}
