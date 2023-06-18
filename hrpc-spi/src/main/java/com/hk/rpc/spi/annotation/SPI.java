package com.hk.rpc.spi.annotation;

import java.lang.annotation.*;

/**
 * @author : HK意境
 * @ClassName : SPI
 * @date : 2023/6/17 18:10
 * @description : 用于标注到加入SPI机制的接口上
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {

    /**
     * SPI 接口实现默认值
     * @return
     */
    String value() default "";

}
