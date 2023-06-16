package com.hk.rpc.proxy.api.object;

import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.header.RpcHeaderFactory;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.proxy.api.async.IAsyncObjectProxy;
import com.hk.rpc.proxy.api.consumer.Consumer;
import com.hk.rpc.proxy.api.future.RPCFuture;
import com.hk.rpc.registry.api.RegistryService;
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
public class ObjectProxy<T> implements InvocationHandler, IAsyncObjectProxy {


    /**
     * 服务注册与发现
     */
    private RegistryService registryService;

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
                       long timeout, RegistryService registryService, Consumer consumer, String serializationType,
                       boolean async, boolean oneway) {
        this.clazz = clazz;
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.timeout = timeout;
        this.registryService = registryService;
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
        RPCFuture rpcFuture = this.consumer.sendRequest(rpcProtocol, this.registryService);

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


    /**
     * 异步调用 代理对象方法
     * @param methodName 方法名称
     * @param args 方法参数
     * @return
     */
    @Override
    public RPCFuture call(String methodName, Object... args) {

        RpcProtocol<RpcRequest> protocol = this.createRequest(this.clazz.getName(), methodName, args);

        RPCFuture future = null;
        // 发起异步调用
        try {
            future = this.consumer.sendRequest(protocol, this.registryService);
        } catch (Exception e) {
            log.error("async rpc call throws exception:", e);
        }

        return future;
    }


    /**
     * 构建请求
     * @param className 接口类名
     * @param methodName 方法名称
     * @param args 目标方法参数集
     * @return
     */
    private RpcProtocol<RpcRequest> createRequest(String className, String methodName, Object[] args) {

        RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
        rpcProtocol.setHeader(RpcHeaderFactory.getRequestHeader(this.serializationType));

        // 构建 请求
        RpcRequest request = new RpcRequest();
        request.setClassName(className).setMethodName(methodName).setParameters(args)
                .setVersion(this.serviceVersion).setGroup(this.serviceGroup);

        // 计算参数类型集合
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = this.getParameterType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
        // debug 日志
        log.debug("rpc request:{}#{},parameterTypes={},parameters={}", className, methodName, Arrays.toString(parameterTypes), Arrays.toString(args));

        rpcProtocol.setBody(request);
        return rpcProtocol;
    }

    /**
     * 获取参数类型
     * @param arg
     * @return
     */
    private Class getParameterType(Object arg) {

        Class<?> classType = arg.getClass();
        String typeName = classType.getName();
        switch (typeName){
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }
        return classType;
    }


}
