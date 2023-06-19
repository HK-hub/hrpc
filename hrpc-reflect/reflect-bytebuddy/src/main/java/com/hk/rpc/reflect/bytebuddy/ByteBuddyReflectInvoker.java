package com.hk.rpc.reflect.bytebuddy;

import com.hk.rpc.reflect.api.ReflectInvoker;
import com.hk.rpc.spi.annotation.SPIClass;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author : HK意境
 * @ClassName : ByteBuddyReflectInvoker
 * @date : 2023/6/19 15:04
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class ByteBuddyReflectInvoker implements ReflectInvoker {

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

        log.debug("use ByteBuddy reflect type invoke method:{}#{},parameterTypes={}, parameters={}", serviceClass.getName(), methodName,
                Arrays.toString(parameterTypes), Arrays.toString(parameters));

        // 获取类
        Class<?> childClass = new ByteBuddy()
                .subclass(serviceClass)
                .make()
                .load(ByteBuddyReflectInvoker.class.getClassLoader())
                .getLoaded();

        // 获取方法
        Object instance = childClass.getDeclaredConstructor().newInstance();
        Method method = childClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);

        // 执行方法
        return method.invoke(instance, parameters);
    }
}
