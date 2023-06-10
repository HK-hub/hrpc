package com.hk.rpc.codec;


import com.hk.rpc.serialization.api.Serialization;
import com.hk.rpc.serialization.jdk.JdkSerialization;

/**
 * @author : HK意境
 * @ClassName : RpcCodec
 * @date : 2023/6/10 15:11
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public interface RpcCodec {

    default Serialization getJdkSerialization() {
        return new JdkSerialization();
    }


}
