package com.hk.rpc.reflect.asm;

import com.hk.rpc.reflect.api.ReflectInvoker;
import com.hk.rpc.reflect.asm.proxy.ReflectProxy;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author : HK意境
 * @ClassName : AsmReflectInvoker
 * @date : 2023/6/19 15:04
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class AsmReflectInvoker implements ReflectInvoker {

    /**
     * ByteBuddy 实现反射调用真实方法
     * @param serviceBean 服务实例
     * @param serviceClass 服务实例类型
     * @param methodName 真实方法名称
     * @param parameterTypes 方法参数类型数组
     * @param parameters 参数数组
     * @return
     * @throws Exception
     */
    @Override
    public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {

        log.debug("use ASM reflect type invoke method:{}#{},parameterTypes={}, parameters={}", serviceClass.getName(), methodName,
                Arrays.toString(parameterTypes), Arrays.toString(parameters));

        // 获取类
        Constructor<?> constructor = serviceClass.getConstructor(new Class[]{});
        Object[] constructorParameters = new Object[]{};
        Object proxyInstance = ReflectProxy.newProxyInstance(AsmReflectInvoker.class.getClassLoader(), this.getInvocationHandler(serviceBean), serviceClass, constructor, constructorParameters);

        // 获取方法
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);

        // 执行方法
        return method.invoke(proxyInstance, parameters);
    }



    private InvocationHandler getInvocationHandler(Object serviceBean) {
        return (proxy, method, args) -> {
            method.setAccessible(true);
            return method.invoke(serviceBean, args);
        };
    }
}
