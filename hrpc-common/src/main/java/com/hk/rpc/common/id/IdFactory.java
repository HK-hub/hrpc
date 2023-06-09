package com.hk.rpc.common.id;

import com.sun.xml.internal.bind.v2.model.core.ID;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : HK意境
 * @ClassName : IdFactory
 * @date : 2023/6/9 22:51
 * @description : 简易ID工厂类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class IdFactory {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    public static long getId() {
        return ID_GENERATOR.getAndIncrement();
    }
}
