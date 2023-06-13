package com.hk.rpc.protocol.enumeration;

import lombok.Getter;

/**
 * @author : HK意境
 * @ClassName : RpcStatus
 * @date : 2023/6/11 16:06
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Getter
public enum RpcStatus {

    SUCCESS(0),
    FAILURE(1),
    ;

    private int code;


    RpcStatus(int code) {
        this.code = code;
    }
}
