package com.hk.rpc.proxy.api.callback;

/**
 * @author : HK意境
 * @ClassName : DefaultAsyncRPCCallback
 * @date : 2023/6/14 20:00
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class DefaultAsyncRPCCallback implements AsyncRPCCallback{

    /**
     * 执行成功
     * @param result
     */
    @Override
    public void onSuccess(Object result) {

    }


    /**
     * 执行失败
     * @param result
     */
    @Override
    public void onFailure(Object result) {

    }

    /**
     * 执行完成
     * @param message
     */
    @Override
    public void onCompleted(Object message) {

    }


    /**
     * 执行遇到错误或异常
     * @param e
     */
    @Override
    public void onException(Exception e) {

    }
}
