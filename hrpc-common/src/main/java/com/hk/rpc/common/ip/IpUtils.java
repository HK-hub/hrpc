package com.hk.rpc.common.ip;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * @ClassName : IpUtils
 * @author : HK意境
 * @date : 2023/6/26 22:55
 * @description : IP 地址工具类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class IpUtils {

    public static InetAddress getLocalInetAddress()  {
        try{
            return InetAddress.getLocalHost();
        }catch (Exception e){
            log.error("get local ip address throws exception: ", e);
        }
        return null;
    }

    public static String getLocalAddress(){
        return getLocalInetAddress().toString();
    }

    public static String getLocalHostName(){
        return getLocalInetAddress().getHostName();
    }

    public static String getLocalHostIp(){
        return getLocalInetAddress().getHostAddress();
    }
}
