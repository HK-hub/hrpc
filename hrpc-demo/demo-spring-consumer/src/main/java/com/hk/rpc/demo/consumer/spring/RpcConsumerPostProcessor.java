package com.hk.rpc.demo.consumer.spring;

import com.hk.rpc.annotation.RpcReference;
import com.hk.rpc.constants.RpcConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author : HK意境
 * @ClassName : RpcConsumerPostProcessor
 * @date : 2023/7/10 21:40
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RpcConsumerPostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor {

    private ApplicationContext applicationContext;

    private ClassLoader classLoader;

    private final Map<String, BeanDefinition> rpcRefBeanDefinitions = new LinkedHashMap<>();

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }



    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        // 获取所有bean
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();

        // 对 @RpcReference 注解标注的bean 的依赖进行依赖注入设置
        for (String name : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
            String beanClassName = beanDefinition.getBeanClassName();
            if (Objects.nonNull(beanClassName)) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.classLoader);
                ReflectionUtils.doWithFields(clazz, this::parseRpcReference);
            }
        }

        // 注册BeanDefinition
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        this.rpcRefBeanDefinitions.forEach((beanName, beanDefinition) -> {

            // 已经注册了该bean
            if (applicationContext.containsBean(beanName)) {
                throw new IllegalArgumentException("spring context already has a bean named=" + beanName);
            }

            // 注册 bean
            registry.registerBeanDefinition(beanName, this.rpcRefBeanDefinitions.get(beanName));
            log.info("register RpcReferenceBean:{} success ", beanName);
        });
    }

    /**
     * 解析标注了 @RpcReference 注解的依赖成员
     * @param field
     */
    private void parseRpcReference(Field field) {

        // 获取依赖注入bean 上的 rpcReference 注解
        RpcReference annotation = AnnotationUtils.getAnnotation(field, RpcReference.class);

        if (Objects.nonNull(annotation)) {
            // 注解存在，表明是需要进行远程调用的 bean
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);
            builder.setInitMethodName(RpcConstants.INIT_METHOD_NAME)
                    .addPropertyValue("interfaceClass", field.getType())
                    .addPropertyValue("interfaceClassName", field.getType().getName())
                    .addPropertyValue("version", annotation.version())
                    .addPropertyValue("group", annotation.group())
                    .addPropertyValue("registryType", annotation.registryType())
                    .addPropertyValue("registryAddress", annotation.registryAddress())
                    .addPropertyValue("loadBalanceType", annotation.loadBalanceType())
                    .addPropertyValue("serializationType", annotation.serializationType())
                    .addPropertyValue("timeout", annotation.timeout())
                    .addPropertyValue("async", annotation.asysnc())
                    .addPropertyValue("oneway", annotation.oneway())
                    .addPropertyValue("proxyType", annotation.proxy())
                    .addPropertyValue("heartbeatInterval", annotation.heartbeatInterval())
                    .addPropertyValue("scanInactiveChannelInterval", annotation.scanInactiveChannelInterval())
                    .addPropertyValue("retryTimes", annotation.retryTimes())
                    .addPropertyValue("retryInterval", annotation.retryInterval());

            BeanDefinition beanDefinition = builder.getBeanDefinition();

            // 放入缓存中
            this.rpcRefBeanDefinitions.put(field.getName(), beanDefinition);
        }

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
