package com.hk.rpc.spi.factory;

import com.hk.rpc.spi.annotation.SPI;
import com.hk.rpc.spi.annotation.SPIClass;
import com.hk.rpc.spi.loader.ExtensionLoader;

import java.util.Optional;

/**
 * @author : HK意境
 * @ClassName : SpiExtensionFactory
 * @date : 2023/6/17 18:16
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SPIClass
public class SpiExtensionFactory implements ExtensionFactory {


    @Override
    public <T> T getExtension(String key, Class<T> clazz) {

        return Optional.ofNullable(clazz)
                // 接口
                .filter(Class::isInterface)
                // @SPI 注解标注
                .filter(cls -> cls.isAnnotationPresent(SPI.class))
                .map(ExtensionLoader::getExtensionLoader)
                .map(ExtensionLoader::getDefaultSpiClassInstance)
                .orElse(null);

    }
}
