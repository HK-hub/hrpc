package com.hk.rpc.serialization.api;

/**
 * @author : HK意境
 * @ClassName : Serialization
 * @date : 2023/6/10 14:23
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public interface Serialization {

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    public <T> byte[] serialize(T obj);


    /**
     * 反序列化
     * @param data 二进制数据
     * @param clazz 目标类
     * @param <T> 实例
     * @return
     */
    public <T> T deserialize(byte[] data, Class<T> clazz);

}

