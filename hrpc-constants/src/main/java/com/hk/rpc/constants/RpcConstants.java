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

    /**
     * 基于随机算法的负载均衡
     */
    public static final String SERVICE_LOAD_BALANCER_RANDOM = "random";


    /**
     * 基于加权随机算法的负载均衡
     */
    public static final String SERVICE_LOAD_BALANCER_RANDOM_WEIGHT = "randomWeight";

    /**
     * 基于轮询算法的负载均衡
     */
    public static final String SERVICE_LOAD_BALANCER_ROBIN = "robin";


    /**
     * 基于加权轮询算法的负载均衡
     */
    public static final String SERVICE_LOAD_BALANCER_ROBIN_WEIGHT = "robinWeight";

    /**
     * 基于HASH算法的负载均衡
     */
    public static final String SERVICE_LOAD_BALANCER_HASH = "hash";

    /**
     * 基于加权HASH算法的负载均衡
     */
    public static final String SERVICE_LOAD_BALANCER_HASH_WEIGHT = "hashWeight";


    /**
     * 基于源IP地址Hash的负载均衡
     */
    public static final String SERVICE_LOAD_BALANCER_SOURCE_IP_HASH = "ipHash";

    /**
     * 基于源IP地址加权Hash的负载均衡
     */
    public static final String SERVICE_LOAD_BALANCER_SOURCE_IP_HASH_WEIGHT = "ipHashWeight";

    /**
     * 基于一致性HASH的负载均衡策略
     */
    public static final String SERVICE_LOAD_BALANCER_CONSISTENT_HASH = "consistentHash";

    /**
     * 最小连接数负载均衡
     */
    public static final String SERVICE_LOAD_BALANCER_LEAST_CONNECTIONS = "leastConnections";


    /**
     * 增强型负载均衡前缀
     */
    public static final String SERVICE_ENHANCED_LOAD_BALANCER_PREFIX = "enhanced_";


    /**
     * 服务实例最小权重
     */
    public static final int SERVICE_WEIGHT_MIN = 1;

    /**
     * 服务实例最大权重
     */
    public static final int SERVICE_WEIGHT_MAX = 100;

    /**
     * 心跳消息
     */
    public static final String HEARTBEAT_PING = "ping";


    /**
     * 心跳消息
     */
    public static final String HEARTBEAT_PONG = "pong";


    /**
     * decoder
     */
    public static final String CODEC_DECODER = "decoder";

    /**
     * encoder
     */
    public static final String CODEC_ENCODER = "encoder";

    /**
     * handler
     */
    public static final String CODEC_HANDLER = "handler";

    /**
     * server-idle-handler
     */
    public static final String CODEC_SERVER_IDLE_HANDLER = "server-idle-handler";

    /**
     * client-idle-handler
     */
    public static final String CODEC_CLIENT_IDLE_HANDLER = "client-idle-handler";

    /**
     * 扫描结果缓存的时间间隔，默认为1秒，单位为毫秒
     */
    public static final int RPC_SCAN_RESULT_CACHE_TIME_INTERVAL = 1000;

    /**
     * 默认的结果缓存时长，默认5秒，单位是毫秒
     */
    public static final int RPC_SCAN_RESULT_CACHE_EXPIRE = 5000;



    public static void main(String[] args){
        String str = "test0000000000000000";
        System.out.println(str.replace("0", ""));
    }


}
