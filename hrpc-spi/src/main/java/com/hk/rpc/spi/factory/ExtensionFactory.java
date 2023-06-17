package com.hk.rpc.spi.factory;

import com.hk.rpc.spi.annotation.SPI;

/**
 * @author : HK意境
 * @ClassName : ExtensionFactory
 * @date : 2023/6/17 18:13
 * @description : 扩展类加载器的工厂接口
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SPI("spi")
public interface ExtensionFactory {

    /**
     * 获取扩展类对象
     * @param key 传入的key值
     * @param clazz 对象class
     * @param <T> 泛型类型
     * @return
     */
    public <T> T getExtension(String key, Class<T> clazz);


}
