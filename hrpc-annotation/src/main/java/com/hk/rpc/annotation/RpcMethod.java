package com.hk.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : HK意境
 * @ClassName : RpcMethod
 * @date : 2023/7/7 18:32
 * @description : 标注再接口方法上进行方法的单独配置
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcMethod {

    /**
     * 重试次数：考虑到RPC接口的幂等性问题，
     * @return
     */
    int retryTimes() default 0;


}
