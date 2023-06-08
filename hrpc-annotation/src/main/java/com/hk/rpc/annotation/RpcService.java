package com.hk.rpc.annotation;

import lombok.ToString;
import org.springframework.stereotype.Component;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : HK意境
 * @ClassName : RpcService
 * @date : 2023/6/8 14:45
 * @description : 服务提供者核心注解类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {


    /**
     * 接口的class类
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 接口的 ClassName
     */
    String interfaceClassName() default "";

    /**
     * 版本号
     */
    String version() default "1.0.0";

    /**
     * 服务分组
     */
    String group() default "";

}
