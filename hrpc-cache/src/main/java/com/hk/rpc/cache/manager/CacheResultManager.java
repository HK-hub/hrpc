package com.hk.rpc.cache.manager;

import com.hk.rpc.cache.result.CacheResultKey;
import com.hk.rpc.constants.RpcConstants;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author : HK意境
 * @ClassName : CacheResultManager
 * @date : 2023/7/14 17:20
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class CacheResultManager<T> {

    /**
     * 缓存调用结果的map
     */
    private final Map<CacheResultKey, T> cacheResult = new ConcurrentHashMap<>(4096);

    /**
     * 扫描缓存结果是否过期的线程池
     */
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    /**
     * 读写锁
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    /**
     * 读锁
     */
    private final Lock readLock = lock.readLock();


    /**
     * 写锁
     */
    private final Lock writeLock = lock.writeLock();


    /**
     * 缓存结果过期时长
     */
    private long resultCacheExpire;

    /**
     * 实例对象
     */
    private static volatile CacheResultManager instance;


    private CacheResultManager(long resultCacheExpire, boolean enableResultCache) {

        this.resultCacheExpire = resultCacheExpire;
        if (enableResultCache) {
            this.startScanTask();
        }
    }


    /**
     * 扫描缓存中的数据是否过期，过期进行数据清除
     */
    private void startScanTask() {

        this.executorService.scheduleAtFixedRate(() -> {
            if (this.cacheResult.size() > 0) {
                try{
                    // 加写锁
                    this.writeLock.lock();
                    Iterator<Map.Entry<CacheResultKey, T>> iterator = this.cacheResult.entrySet().iterator();

                    // 对每一个结果进行缓存时间是否过期进行处理
                    while (iterator.hasNext()) {
                        Map.Entry<CacheResultKey, T> next = iterator.next();
                        CacheResultKey key = next.getKey();

                        // 当前时间 - 创建时间 > 过期时长, 则需要进行缓存数据清除
                        if (System.currentTimeMillis() - key.getCacheTimestamp() > this.resultCacheExpire) {
                            this.cacheResult.remove(key);
                        }
                    }
                }finally {
                    this.writeLock.unlock();
                }
            }
        }, 0, RpcConstants.RPC_SCAN_RESULT_CACHE_TIME_INTERVAL, TimeUnit.MILLISECONDS);


    }


    public T get(CacheResultKey key) {

        return this.cacheResult.get(key);
    }


    public void put(CacheResultKey key, T value) {

        try {
            this.writeLock.lock();
            this.cacheResult.put(key, value);
        } finally {
            this.writeLock.unlock();
        }
    }


    /**
     * 单例模式
     * @param resultCacheExpire
     * @param enableResultCache
     * @param <T>
     * @return
     */
    public static <T> CacheResultManager<T> getInstance(long resultCacheExpire, boolean enableResultCache) {

        if (Objects.isNull(instance)) {
            synchronized (CacheResultManager.class) {
                if (Objects.isNull(instance)) {
                    instance = new CacheResultManager<T>(resultCacheExpire, enableResultCache);
                }
            }
        }
        return instance;
    }

}
