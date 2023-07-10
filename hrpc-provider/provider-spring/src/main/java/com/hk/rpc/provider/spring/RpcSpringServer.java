package com.hk.rpc.provider.spring;

import com.hk.rpc.annotation.RpcService;
import com.hk.rpc.common.helper.RpcServiceHelper;
import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.protocol.meta.ServiceMeta;
import com.hk.rpc.provider.common.server.base.BaseServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.util.Map;

/**
 * @author : HK意境
 * @ClassName : RpcSpringServer
 * @date : 2023/7/8 22:05
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RpcSpringServer extends BaseServer implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;


    /**
     * 指定地址，端口
     *
     * @param address
     * @param port
     * @param reflectType
     * @param registryAddress
     * @param registryLoadBalanceType
     * @param registryType
     * @param heartbeatInterval
     * @param scanInactiveInterval
     */
    public RpcSpringServer(String address, int port, String reflectType, String registryAddress, String registryLoadBalanceType, String registryType, int heartbeatInterval, int scanInactiveInterval) {
        super(address, port, reflectType, registryAddress, registryLoadBalanceType, registryType, heartbeatInterval, scanInactiveInterval);
    }


    /**
     * 启动服务服务提供者
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    /**
     * 设置ApplicationContext 管理的Bean对象中的Rpc Service 接口
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;

        // 获取 @RpcService 接口修饰的 Bean 对象
        Map<String, Object> rpcServiceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);

        // 处理service bean 对象
        if (MapUtils.isNotEmpty(rpcServiceBeanMap)) {
            for (Object bean : rpcServiceBeanMap.values()) {
                // 获取服务元数据
                Class<?> beanClass = bean.getClass();
                RpcService annotation = beanClass.getAnnotation(RpcService.class);
                ServiceMeta serviceMeta = this.buildServiceMetaData(beanClass, annotation);
                // 设置服务地址
                serviceMeta.setServiceAddress(this.address).setPort(this.port);
                this.handlerMap.put(RpcServiceHelper.locationService(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()),
                        bean);

                // 服务注册
                try {
                    this.registryService.register(serviceMeta);
                } catch (Exception e) {
                    log.error("register rpc service={}, throw exception:", serviceMeta.toString(), e);
                }
            }
        }
    }

    /**
     * 构造服务元数据
     * @param beanClass
     * @param annotation
     * @return
     */
    private ServiceMeta buildServiceMetaData(Class<?> beanClass, RpcService annotation) {

        // 构造服务名称
        Class<?> interfaceClass = annotation.interfaceClass();
        String serviceName = interfaceClass.getName();
        if (interfaceClass == void.class) {
            // 没有指定接口名称，采用 className
            String className = annotation.interfaceClassName();
            if (StringUtils.isBlank(className)) {
                // 类名也没有指定 -> 采用第一个继承的接口作为类
                Class<?>[] interfaces = beanClass.getInterfaces();
                if (ArrayUtils.isNotEmpty(interfaces)) {
                    className = interfaces[0].getName();
                }
            }
            serviceName = className;
        }

        // 获取权重
        int weight = annotation.weight();
        if (weight < RpcConstants.SERVICE_WEIGHT_MIN) {
            weight = RpcConstants.SERVICE_WEIGHT_MIN;
        } else if (weight > RpcConstants.SERVICE_WEIGHT_MAX) {
            weight = RpcConstants.SERVICE_WEIGHT_MAX;
        }

        // 返回服务元数据据，
        return new ServiceMeta(serviceName, annotation.version(), annotation.group(),
                null, 6666, weight);
    }


}
