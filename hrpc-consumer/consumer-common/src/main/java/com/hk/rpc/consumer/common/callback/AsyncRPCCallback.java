package com.hk.rpc.consumer.common.callback;

/**
 * @author : HK意境
 * @ClassName : AsyncRPCCallback
 * @date : 2023/6/14 17:10
 * @description : 回调接口
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public interface AsyncRPCCallback {

    /**
     * 成功后回调方法
     * @param result
     */
    public void onSuccess(Object result);

    /**
     * 失败回调方法
     * @param result
     */
    public void onFailure(Object result);

    /**
     * 结束后回调方法
     * @param message
     */
    public void onCompleted(Object message);

    /**
     * 异常回调方法
     * @param e
     */
    public void onException(Exception e);

}
