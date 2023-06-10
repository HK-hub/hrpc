package com.hk.rpc.common.exception;

import java.io.Serializable;

/**
 * @author : HK意境
 * @ClassName : SerializeException
 * @date : 2023/6/10 12:14
 * @description : 序列化异常
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class SerializeException extends RuntimeException{

    private static final long serialVersionUID = -6783134254669118520L;

    public SerializeException(Throwable throwable) {
        super(throwable);
    }

    public SerializeException(final String message) {
        super(message);
    }

    public SerializeException(final String message, Throwable throwable) {
        super(message, throwable);
    }

}
