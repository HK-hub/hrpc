package com.hk.rpc.common.helper;

/**
 * @author : HK意境
 * @ClassName : RpcServiceHelper
 * @date : 2023/6/11 16:59
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcServiceHelper {


    /**
     * 定位服务
     * @param serviceName 服务类名
     * @param version 版本
     * @param group 分组
     * @return 服务名称#版本#分组
     */
    public static String locationService(String serviceName, String version, String group) {

        return String.join("#", serviceName, version, group);
    }


}
