package com.hk.rpc.protocol.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * @author : HK意境
 * @ClassName : ServiceMeta
 * @date : 2023/6/16 14:30
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMeta implements Serializable {

    private static final long serialVersionUID = 5159445528446092605L;

    /**
     * 服务名称
     */
    private String serviceName;


    /**
     * 服务版本
     */
    private String serviceVersion;


    /**
     * 服务分组
     */
    private String serviceGroup;


    /**
     * 服务地址
     */
    private String serviceAddress;


    /**
     * 服务端口
     */
    private int port;


    /**
     * 服务提供者实例权重
     */
    private int weight;

}
