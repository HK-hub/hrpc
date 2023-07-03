package com.hk.rpc.registry.etcd;

import com.alibaba.fastjson2.JSON;
import com.hk.rpc.common.helper.RpcServiceHelper;
import com.hk.rpc.loadbalance.api.ServiceLoadbalancer;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.registry.api.RegistryService;
import com.hk.rpc.registry.api.config.RegistryConfig;
import com.hk.rpc.spi.annotation.SPIClass;
import com.hk.rpc.spi.loader.ExtensionLoader;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : HK意境
 * @ClassName : EtcdRegistryService
 * @date : 2023/7/2 18:41
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class EtcdRegistryService implements RegistryService {


    /**
     * 服务注册到 Etcd 的根路径
     */
    public static final String ETCD_REGISTRY_DIR = "/h_rpc";


    /**
     * 服务配置
     */
    private RegistryConfig registryConfig;

    /**
     * etcd 连接客户端
     */
    private Client client;
    private Lease leaseClient;
    private KV kvClient;

    /**
     * 负载均衡
     */
    private ServiceLoadbalancer<ServiceMeta> serviceLoadbalancer;

    @Override
    public void init(RegistryConfig registryConfig) throws Exception {

        this.registryConfig = registryConfig;
        this.client = Client.builder()
                .endpoints(registryConfig.getRegistryAddress())
                .build();
        this.leaseClient = this.client.getLeaseClient();
        this.kvClient = this.client.getKVClient();

        // 负载均衡
        String loadBalanceType = registryConfig.getRegistryLoadBalanceType();
        this.serviceLoadbalancer = ExtensionLoader.getExtension(ServiceLoadbalancer.class, loadBalanceType);
    }

    /**
     * 注册服务实例
     * @param serviceMeta 服务元数据
     * @throws Exception
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {

        // 租约
        long leaseId = 0;

        try {
            // 租约
            leaseId = leaseClient.grant(60, 60, TimeUnit.SECONDS).get().getID();
        } catch (Exception e) {
            log.error("lease grant exception:", e);
        }

        // 服务实例观察流
        StreamObserver<LeaseKeepAliveResponse> streamObserver = new StreamObserver<LeaseKeepAliveResponse>() {
            @Override
            public void onNext(LeaseKeepAliveResponse leaseKeepAliveResponse) {
                // 心跳数据
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };

        // 续约
        leaseClient.keepAlive(leaseId, streamObserver);

        // 开始准备注册
        // 绑定租约
        PutOption option = PutOption.newBuilder().withLeaseId(leaseId)
                .build();

        // 注册服务元数据
        ByteSequence serviceKey = ByteSequence.from(this.buildServiceKey(serviceMeta), StandardCharsets.UTF_8);
        ByteSequence service = ByteSequence.from(JSON.toJSONString(serviceMeta), StandardCharsets.UTF_8);
        kvClient.put(serviceKey, service, option);
    }


    /**
     * 构建服务实例key
     * @param serviceMeta
     * @return
     */
    private String buildServiceKey(ServiceMeta serviceMeta) {

        // base_dir + group + service + version + host + ":" + port
        StringJoiner joiner = new StringJoiner("/");

        String location = RpcServiceHelper.locationService(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup());

        return joiner.add(ETCD_REGISTRY_DIR)
                .add(location)
                .add(serviceMeta.getServiceAddress())
                .add(":")
                .add(String.valueOf(serviceMeta.getPort()))
                .toString();
    }


    /**
     * 注销服务实例
     * @param serviceMeta 服务元数据
     * @throws Exception
     */
    @Override
    public void unregister(ServiceMeta serviceMeta) throws Exception {

        ByteSequence serviceKey = ByteSequence.from(this.buildServiceKey(serviceMeta), StandardCharsets.UTF_8);

        // 删除服务
        DeleteOption option = DeleteOption.newBuilder().withPrevKV(true).build();
        CompletableFuture<DeleteResponse> deleteFuture = this.kvClient.delete(serviceKey, option);
        DeleteResponse response = deleteFuture.get();
        long lease = response.getPrevKvs().get(0).getLease();

        // 撤销租约
        this.leaseClient.revoke(lease);
    }



    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIp) throws Exception {

        // 获取服务key
        String serviceKey = ETCD_REGISTRY_DIR.concat("/").concat(serviceName);

        // 获取服务实例集合
        CompletableFuture<GetResponse> future = this.kvClient.get(ByteSequence.from(serviceKey, StandardCharsets.UTF_8));
        GetResponse response = future.get();
        List<KeyValue> serviceInstanceList = response.getKvs();

        // 转换为服务元数据列表
        List<ServiceMeta> serviceMetaList = serviceInstanceList.stream().map(kv -> {
            ByteSequence value = kv.getValue();
            return JSON.parseObject(value.getBytes(), ServiceMeta.class);
        }).collect(Collectors.toList());

        // 负载均衡
        ServiceMeta select = this.serviceLoadbalancer.select(serviceMetaList, hashCode(), sourceIp);
        log.debug("load balancer select serviceMeta list element is:{}", select.toString());

        return select;
    }

    @Override
    public void destroy() throws IOException {

        this.leaseClient.close();
        this.kvClient.close();
        this.client.close();
    }
}
