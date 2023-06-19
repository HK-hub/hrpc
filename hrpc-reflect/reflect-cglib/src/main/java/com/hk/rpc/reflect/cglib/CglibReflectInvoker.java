package com.hk.rpc.reflect.cglib;

import com.hk.rpc.reflect.api.ReflectInvoker;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.util.Arrays;

/**
 * @author : HK意境
 * @ClassName : CglibReflectInvoker
 * @date : 2023/6/19 15:04
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class CglibReflectInvoker implements ReflectInvoker {

    /**
     * cglib 实现反射调用真实方法
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

        log.debug("use cglib reflect type invoke method:{}#{},parameterTypes={}, parameters={}", serviceClass.getName(), methodName,
                Arrays.toString(parameterTypes), Arrays.toString(parameters));
        // 获取方法
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod method = serviceFastClass.getMethod(methodName, parameterTypes);

        // 执行方法
        return method.invoke(serviceBean, parameters);
    }
}
