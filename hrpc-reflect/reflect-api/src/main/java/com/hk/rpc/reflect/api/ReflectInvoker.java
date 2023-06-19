package com.hk.rpc.reflect.api;

import com.hk.rpc.spi.annotation.SPI;

/**
 * @author : HK意境
 * @ClassName : ReflectInvoker
 * @date : 2023/6/19 9:33
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SPI
public interface ReflectInvoker {


    /**
     * 调用真实方法的SPI通用接口
     * @param serviceBean 服务实例
     * @param serviceClass 服务实例类型
     * @param methodName 真实方法名称
     * @param parameterTypes 方法参数类型数组
     * @param parameters 参数数组
     * @return 方法调用结果
     * @throws Exception
     */
    Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception;

}
