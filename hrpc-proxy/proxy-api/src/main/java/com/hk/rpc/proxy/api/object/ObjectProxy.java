package com.hk.rpc.proxy.api.object;

import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.header.RpcHeaderFactory;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.proxy.api.consumer.Consumer;
import com.hk.rpc.proxy.api.future.RPCFuture;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : HK意境
 * @ClassName : ObjectProxy
 * @date : 2023/6/14 23:10
 * @description : 动态代理类执行类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@Data
@Accessors(chain = true)
public class ObjectProxy<T> implements InvocationHandler {

    /**
     * 接口的class 对象
     */
    private Class<T> clazz;


    /**
     * 服务版本号
     */
    private String serviceVersion;

    /**
     * 服务分组
     */
    private String serviceGroup;


    /**
     * 默认超时时间: 15秒
     */
    private long timeout = 15000L;


    /**
     * 服务消费者
     */
    private Consumer consumer;


    /**
     * 序列化类型
     */
    private String serializationType;


    /**
     * 是否异步化调用
     */
    private boolean async;

    /**
     * 是否单向调用
     */
    private boolean oneway;


    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }


    public ObjectProxy(Class<T> clazz, String serviceVersion, String serviceGroup,
                       long timeout, Consumer consumer, String serializationType,
                       boolean async, boolean oneway) {
        this.clazz = clazz;
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.timeout = timeout;
        this.consumer = consumer;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }


    /**
     * 动态代理执行RPC 调用
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        // 判断是否 Object 类的默认方法：如果调用的是这些基本方法，则无需进行 rpc 调用
        if (Object.class == method.getDeclaringClass()) {
            return ensureObjectNativeMethod(proxy, method, args);
        }

        // 非 Object 类方法, 执行 RPC 调用
        RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
        rpcProtocol.setHeader(RpcHeaderFactory.getRequestHeader(this.serializationType));

        RpcRequest request = new RpcRequest();
        request.setVersion(this.serviceVersion)
                        .setGroup(this.serviceGroup);
        request.setClassName(method.getDeclaringClass().getName())
                .setMethodName(method.getName())
                .setParameterTypes(method.getParameterTypes())
                .setParameters(args);
        request.setAsync(this.async).setOneway(this.oneway);

        // 设置rpc 请求协议
        rpcProtocol.setBody(request);

        // debug rpc 调用日志
        log.debug("rpc call method:{}#{}", method.getDeclaringClass().getName(), method.getName());
        log.debug("rpc request method:parameterTypes={},parameters={}", Arrays.toString(method.getParameterTypes()), Arrays.toString(args));

        // 执行 RPC 调用
        RPCFuture rpcFuture = this.consumer.sendRequest(rpcProtocol);

        // 更具调用方式来获取调用结果
        if (Objects.isNull(rpcFuture)) {
            return null;
        }

        // 是否设置超时时间
        if (this.timeout > 0) {
            // 有超时时间
            return rpcFuture.get(this.timeout, TimeUnit.MILLISECONDS);
        }

        // 没有超时时间,阻塞获取
        return rpcFuture.get();
    }


    /**
     * 判断是否Object的原生方法
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    private Object ensureObjectNativeMethod(Object proxy, Method method, Object[] args) {
        String name = method.getName();

        if ("equals".equals(name)) {

            // 执行 equals 方法
            return proxy == args[0];
        } else if ("hashCode".equals(name)) {

            // 执行 hashCode 方法
            return System.identityHashCode(proxy);
        } else if ("toString".equals(name)) {

            // 执行 toString 方法
            return proxy.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(proxy)) + ", with InvocationHandler " + this;
        } else {

            // Object 中的其他方法，暂不支持
            throw new IllegalStateException(String.valueOf(method));
        }
    }


}
