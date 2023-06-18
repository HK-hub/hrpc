package com.hk.rpc.codec;


import com.hk.rpc.serialization.api.Serialization;
import com.hk.rpc.serialization.jdk.JdkSerialization;
import com.hk.rpc.spi.loader.ExtensionLoader;

/**
 * @author : HK意境
 * @ClassName : RpcCodec
 * @date : 2023/6/10 15:11
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public interface RpcCodec {

    /**
     * 根据序列化类型通过 SPI获取序列化句柄
     * @param serializationType 序列化类型
     * @return
     */
    default Serialization getSerialization(String serializationType) {
        return ExtensionLoader.getExtension(Serialization.class, serializationType);
    }


}
