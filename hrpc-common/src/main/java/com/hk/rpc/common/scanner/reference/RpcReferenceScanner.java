package com.hk.rpc.common.scanner.reference;

import com.hk.rpc.annotation.RpcReference;
import com.hk.rpc.common.scanner.ClassScanner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author : HK意境
 * @ClassName : RpcReferenceScanner
 * @date : 2023/6/8 20:50
 * @description : 服务消费者注解扫描器
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RpcReferenceScanner extends ClassScanner {

    /**
     * 扫描指定包下的类，并筛选使用@RpcReference 标注的类
     *
     * @param host            注册中心地址
     * @param port            注册中心端口
     * @param scanPackage     扫描包名
     * @param registryService 注册服务
     *
     * @return
     */
    public static Map<String, Object> doScannerWithRpcReferenceAnnotationFilter(
            String host, int port, String scanPackage/*, RegistryServiceScanner registryService**/) throws IOException {

        HashMap<String, Object> handlerMap = new HashMap<>();

        // 扫描包下所有类
        List<String> classNameList = getClassNameList(scanPackage);
        if (CollectionUtils.isEmpty(classNameList)) {
            return handlerMap;
        }

        classNameList.forEach(RpcReferenceScanner::filterRpcReferenceAndBuildMetaData);

        return handlerMap;
    }


    /**
     * 扫描筛选rpc消费者并且获取元数据注册信息到注册中心
     *
     * @param className
     */
    private static void filterRpcReferenceAndBuildMetaData(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            // 获取类属性字段
            Field[] fields = clazz.getDeclaredFields();
            // 筛选出 @RpcReference 标注的字段
            Stream.of(fields).forEach(field -> {
                RpcReference annotation = field.getAnnotation(RpcReference.class);
                if (Objects.nonNull(annotation)) {
                    // TODO 后续处理逻辑: 将@RpcReference 注解标注的接口引用代理对象放入全局缓存中
                    log.info("当前标注了@RpcReference注解的字段名称:{}-{}", className, field.getName());

                    // 获取元数据属性
                    String registryAddress = annotation.registryAddress();
                    String registryType = annotation.registryType();
                    String group = annotation.group();
                    String version = annotation.version();

                    log.info("@RpcReference 注解标注字段属性信息如下:");
                    log.info("version==>>>{}", version);
                    log.info("group==>>>{}", group);
                    log.info("registryAddress==>>>{}", registryAddress);
                    log.info("registryType==>>>{}", registryType);
                }
            });

        } catch (Exception e) {
            log.error("scan rpc reference throw exception:", e);
        }
    }

}
