package com.hk.rpc.common.scanner.server;

import com.hk.rpc.annotation.RpcService;
import com.hk.rpc.common.scanner.ClassScanner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import java.io.IOException;
import java.util.*;

/**
 * @author : HK意境
 * @ClassName : RpcServiceScanner
 * @date : 2023/6/8 19:37
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RpcServiceScanner extends ClassScanner {

    /**
     * 扫描指定包下面的类，并筛选使用@RpcService 注解标注的类
     * @return
     */
    public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(
            String host, int port, String scanPackage /*, RegistryServiceScanner registryService**/) throws IOException {

        Map<String, Object> map = new HashMap<>();

        // 扫描包下获取所有类名集合
        List<String> classNameList = getClassNameList(scanPackage);
        if (CollectionUtils.isEmpty(classNameList)) {
            return map;
        }

        // 处理所有类筛选出服务提供者类
        classNameList.forEach(RpcServiceScanner::filterClassAndBuildMetaData);

        return map;
    }


    /**
     * 筛选出RPC服务提供者并且获取元数据，注册服务到注册中心
     * @param className 全类名
     */
    private static void filterClassAndBuildMetaData(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            // 获取注解
            RpcService annotation = clazz.getAnnotation(RpcService.class);
            if (Objects.nonNull(annotation)) {
                // 注解存在，进行元数据获取
                // 首先使用 interfaceClass, interfaceClass为空，在使用interfaceClassName, 如果两个都没有，则通过获取interface接口来进行确定
                String interfaceClass = annotation.interfaceClass().getName();
                String interfaceClassName = annotation.interfaceClassName();
                String version = annotation.version();
                String group = annotation.group();

                // 如果 interfaceClass 和 interfaceClassName 都为空则进行获取实现的 interface 接口
                Class<?>[] interfaces = clazz.getInterfaces();
                if (interfaces.length == 1) {
                    // 仅仅实现一个接口尝试...
                    Class<?> parent = interfaces[0];
                    interfaceClass = parent.getName();
                    interfaceClassName = parent.getName();
                }

                // TODO 后续向注册中心注册服务元数据，同时向handlerMap 中记录标注了RpcService 注解的类实例。
                log.info("当前标注了@RpcService的类实例名称:{}",  clazz.getName());
                log.info("@RpcService注解上标注的属性信息如下:");
                log.info("interfaceClass==>>>{}", interfaceClass);
                log.info("interfaceClassName==>>>{}", interfaceClassName);
                log.info("version==>>>{}", version);
                log.info("group==>>>{}", group);

            }
        } catch (Exception e) {
            log.error("scan rpc service classes throw exception:", e);
        }
    }


}
