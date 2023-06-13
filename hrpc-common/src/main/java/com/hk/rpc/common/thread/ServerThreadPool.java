package com.hk.rpc.common.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : HK意境
 * @ClassName : ServerThreadPool
 * @date : 2023/6/11 16:24
 * @description : 服务提供者端执行异步任务
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ServerThreadPool {

    private static ThreadPoolExecutor executor;

    static {
        // 初始化
        // IO密集型处理：threads = core * 2;
        int core = Runtime.getRuntime().availableProcessors();

        executor = new ThreadPoolExecutor(core * 2, core * 2, 600L, TimeUnit.SECONDS,
                // 阻塞队列
                new ArrayBlockingQueue<>(65536),
                // 线程工程
                new NamedThreadFactory("RpcServerPool")
        );
    }


    public static void submit(Runnable runnable) {
        executor.submit(runnable);
    }

    public static void shutdown() {
        executor.shutdown();
    }

}
