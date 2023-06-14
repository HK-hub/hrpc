package com.hk.rpc.consumer.common.future;

import com.hk.rpc.common.thread.ClientThreadPool;
import com.hk.rpc.consumer.common.callback.AsyncRPCCallback;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.protocol.response.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : HK意境
 * @ClassName : RPCFuture
 * @date : 2023/6/14 14:35
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class RPCFuture extends CompletableFuture<Object> {

    /**
     * 内部类
     */
    private Sync sync;

    private RpcProtocol<RpcRequest> requestRpcProtocol;

    private RpcProtocol<RpcResponse> responseRpcProtocol;

    /**
     * 回调接口
     */
    private List<AsyncRPCCallback> pendingCallbacks = new ArrayList<>();

    /**
     * 添加回调方法和执行回调时进行加锁和解锁
     */
    private ReentrantLock lock = new ReentrantLock();


    /**
     * RPC 开始时间
     */
    private long startTime;

    /**
     * 响应超时时间: 5000 ms
     */
    private long responseTimeThreshold = 5000L;


    public RPCFuture(RpcProtocol<RpcRequest> requestRpcProtocol) {

        this.sync = new Sync();
        this.requestRpcProtocol = requestRpcProtocol;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 是否结束
     * @return
     */
    @Override
    public boolean isDone() {
        return this.sync.isDone();
    }


    /**
     * 获取
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public Object get() throws InterruptedException, ExecutionException {

        this.sync.tryAcquire(-1);

        // 响应结果是否准备就绪
        if (this.responseRpcProtocol != null) {
            // 已经准备好了
            return this.responseRpcProtocol.getBody().getResult();
        } else {
            // 还没有执行结束
            return null;
        }
    }


    /**
     * 超时阻塞获取
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        boolean success = this.sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (Objects.nonNull(this.responseRpcProtocol)) {
                return this.responseRpcProtocol.getBody().getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Timeout exception. Request id: " + this.requestRpcProtocol.getHeader().getRequestId()
                    + ". Request class name: " + this.requestRpcProtocol.getBody().getClassName()
                    + ". Request method: " + this.requestRpcProtocol.getBody().getMethodName());
        }
    }


    /**
     * 执行回调方法
     * @param callback 回调接口
     */
    private void runCallback(final AsyncRPCCallback callback) {

        RpcResponse rpcResponse = this.responseRpcProtocol.getBody();

        // 异步执行回调
        ClientThreadPool.submit(() -> {
            // 首先执行 完成回调
            callback.onCompleted(this.responseRpcProtocol);

            // 执行成功，失败，异常等回调
            if (rpcResponse.isSuccess()) {
                callback.onSuccess(rpcResponse);
            }  else {
                // 是否失败
                if (BooleanUtils.isTrue(rpcResponse.isFailure())) {
                    callback.onFailure(rpcResponse);
                }

                // 执行 错误异常回调
                callback.onException(new RuntimeException("Response error:", new Throwable(rpcResponse.getMessage())));
            }
        });
    }


    /**
     * 外部服务添加接口回调实例对象到 pendingCallbacks 集合中
     * @param callback
     * @return
     */
    public RPCFuture addCallback(AsyncRPCCallback callback) {
        lock.lock();
        try{
            if (this.isDone()) {
                // 执行回调
                runCallback(callback);
            } else {
                this.pendingCallbacks.add(callback);
            }
        }catch(Exception e){

        }finally {
            lock.unlock();
        }
        return this;
    }


    /**
     * 依次执行 callbacks 集合中的回调方法
     */
    private void invokeCallbacks() {
        lock.lock();
        try{
            for (AsyncRPCCallback callback : this.pendingCallbacks) {
                runCallback(callback);
            }
        }catch(Exception e){
            log.error("execute rpc response callback function error:", e);

        }finally {
            lock.unlock();
        }
    }


    @Override
    public boolean isCancelled() {
        return super.isCancelled();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }


    /**
     * 调用完成结束
     * @param responseRpcProtocol
     */
    public void done(RpcProtocol<RpcResponse> responseRpcProtocol) {

        this.responseRpcProtocol = responseRpcProtocol;
        this.sync.release(1);

        // 执行调用回调
        this.invokeCallbacks();

        // Threshold
        long responseTime = System.currentTimeMillis() - this.startTime;

        // 响应时间大于超时时间
        if (responseTime > responseTimeThreshold) {
            log.warn("Service response time is too slow. Request id = " + responseRpcProtocol.getHeader().getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }


    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        // future status
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int acquires) {
            return getState() == this.done;
        }

        @Override
        protected boolean tryRelease(int releases) {
            if (getState() == this.pending) {
                return compareAndSetState(pending, done);
            }
            return false;
        }

        public boolean isDone() {
            getState();
            return getState() == this.done;
        }

    }

}
