package com.hk.rpc.common.exception;

/**
 * @ClassName : RegistryException
 * @author : HK意境
 * @date : 2023/6/16 22:01
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RegistryException extends RuntimeException {
    private static final long serialVersionUID = -6783134254669118520L;

    /**
     * Instantiates a new Serializer exception.
     *
     * @param e the e
     */
    public RegistryException(final Throwable e) {
        super(e);
    }

    /**
     * Instantiates a new Serializer exception.
     *
     * @param message the message
     */
    public RegistryException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new Serializer exception.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    public RegistryException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
