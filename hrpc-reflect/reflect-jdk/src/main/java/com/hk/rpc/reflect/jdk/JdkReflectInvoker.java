package com.hk.rpc.reflect.jdk;

import com.hk.rpc.reflect.api.ReflectInvoker;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author : HK意境
 * @ClassName : JdkReflectInvoker
 * @date : 2023/6/19 14:16
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class JdkReflectInvoker implements ReflectInvoker {


    /**
     * JDK 实现反射调用
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

        log.debug("use jdk reflect type invoke method:{}#{},parameterTyps={}, parameters={}", serviceClass.getName(), methodName,
                Arrays.toString(parameters), Arrays.toString(parameters));

        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);

        return method.invoke(serviceBean, parameters);
    }
}
