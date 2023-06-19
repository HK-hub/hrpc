package com.hk.rpc.constants;

/**
 * @author : HK意境
 * @ClassName : RpcConstants
 * @date : 2023/6/9 22:55
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcConstants {

    /**
     * 消息头固定长度 32 字节
     */
    public static final int HEADER_TOTAL_LENGTH = 32;

    /**
     * 魔术
     */
    public static final short MAGIC = 0x10;

    /**
     * 版本号
     */
    public static final byte VERSION = 0x1;

    /**
     * 反射类型：jdk
     */
    public static final String REFLECT_TYPE_JDK = "jdk";

    /**
     * 反射类型：cglib
     */
    public static final String REFLECT_TYPE_CGLIB = "cglib";

    /**
     * 反射类型：javassist
     */
    public static final String REFLECT_TYPE_JAVASSIST = "javassist";

    /**
     * 反射类型: ByteBuddy
     */
    public static final String REFLECT_TYPE_BYTEBUDDY = "bytebuddy";

    /**
     * 反射类型: ASM
     */
    public static final String REFLECT_TYPE_ASM = "asm";


    /**
     * JDK动态代理
     */
    public static final String PROXY_JDK = "jdk";

    /**
     * javassist 动态代理
     */
    public static final String PROXY_JAVASSIST = "javassist";

    /**
     * cglib 动态代理
     */
    public static final String PROXY_CGLIB = "cglib";

    /**
     * ByteBuddy 动态代理
     */
    public static final String PROXY_BYTEBUDDY = "bytebuddy";

    /**
     * ASM 动态代理
     */
    public static final String PROXY_ASM = "asm";


    /**
     * 初始化的方法
     */
    public static final String INIT_METHOD_NAME = "init";

    /**
     * zookeeper
     */
    public static final String REGISTRY_CENTER_ZOOKEEPER = "zookeeper";
    /**
     * nacos
     */
    public static final String REGISTRY_CENTER_NACOS = "nacos";
    /**
     * apoll
     */
    public static final String REGISTRY_CENTER_APOLL = "apoll";
    /**
     * etcd
     */
    public static final String REGISTRY_CENTER_ETCD = "etcd";

    /**
     * eureka
     */
    public static final String REGISTRY_CENTER_EUREKA = "eureka";

    /**
     * protostuff 序列化
     */
    public static final String SERIALIZATION_PROTOBUF = "protobuf";
    /**
     * FST 序列化
     */
    public static final String SERIALIZATION_FST = "fst";
    /**
     * hessian2 序列化
     */
    public static final String SERIALIZATION_HESSIAN2 = "hessian2";
    /**
     * jdk 序列化
     */
    public static final String SERIALIZATION_JDK = "jdk";
    /**
     * json 序列化
     */
    public static final String SERIALIZATION_JSON = "json";
    /**
     * kryo 序列化
     */
    public static final String SERIALIZATION_KRYO = "kryo";
    /**
     * 基于ZK的一致性Hash负载均衡
     */
    public static final String SERVICE_LOAD_BALANCER_ZKCONSISTENTHASH = "zkconsistenthash";

    public static void main(String[] args){
        String str = "test0000000000000000";
        System.out.println(str.replace("0", ""));
    }


}
