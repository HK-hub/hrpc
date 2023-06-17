package com.hk.rpc.spi.annotation;

import java.lang.annotation.*;

/**
 * @author : HK意境
 * @ClassName : SPIClass
 * @date : 2023/6/17 18:11
 * @description : 标注到加入SPI机制的接口的实现类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPIClass {
}
