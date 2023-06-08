package com.hk.rpc.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : HK意境
 * @ClassName : RpcReference
 * @date : 2023/6/8 14:59
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Autowired
public @interface RpcReference {

    /**
     * 版本号
     */
    String version() default "1.0.0";

    /**
     * 服务分组
     */
    String group() default "";


    /**
     * 注册中心类型: 目前的类型包含：zookeeper，nacos, etcd, consul
     */
    String registryType() default "zookeeper";

    /**
     * 注册中心地址：ip:port
     */
    String registryAddress() default "127.0.0.1:2181";


    /**
     * 负载均衡类型：默认基于ZK的一致性哈希
     */
    String loadBalanceType() default "zkconsistenthash";


    /**
     * 序列化类型：默认protostuff， 目前的类型包含：protostuff，kryo, json, jdk, hessian2, fst
     */
    String serializationType() default "protostuff";

    /**
     * 超时时间：默认5秒
     */
    long timeout() default 5000;


    /**
     * 是否异步执行
     */
    boolean asysnc() default false;


    /**
     * 是否单向调用
     */
    boolean oneway() default false;


    /**
     * 代理类型：jdk,javassist,cglib,
     */
    String proxy() default "jdk";

}
