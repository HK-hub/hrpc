package com.hk.rpc.registry.api.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : HK意境
 * @ClassName : RegistryConfig
 * @date : 2023/6/16 14:37
 * @description : 注册中心配置类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Data
@Accessors(chain = true)
public class RegistryConfig implements Serializable {

    private static final long serialVersionUID = 7710146464874363301L;

    /**
     * 注册地址
     */
    private String registryAddress;


    /**
     * 注册类型
     */
    private String registryType;


    public RegistryConfig(String registryAddress, String registryType) {
        this.registryAddress = registryAddress;
        this.registryType = registryType;
    }
}
