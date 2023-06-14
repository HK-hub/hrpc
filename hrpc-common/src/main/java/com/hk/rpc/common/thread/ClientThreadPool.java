package com.hk.rpc.common.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : HK意境
 * @ClassName : ClientThreadPool
 * @date : 2023/6/14 17:15
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ClientThreadPool {

    private static ThreadPoolExecutor executor;

    static {
        // 获取可用core
        int core = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(core * 2, core * 2, 600L,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(65536), new NamedThreadFactory("rpc-client-"));
    }

    public static void submit(Runnable task){
        executor.submit(task);
    }

    public static void shutdown() {
        executor.shutdown();
    }


}
