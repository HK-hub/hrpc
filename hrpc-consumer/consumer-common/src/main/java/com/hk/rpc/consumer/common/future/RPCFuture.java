package com.hk.rpc.consumer.common.future;

import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.protocol.response.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

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
