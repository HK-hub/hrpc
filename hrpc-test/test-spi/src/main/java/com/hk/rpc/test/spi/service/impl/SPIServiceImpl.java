package com.hk.rpc.test.spi.service.impl;

import com.hk.rpc.spi.annotation.SPIClass;
import com.hk.rpc.test.spi.service.SPIService;

/**
 * @author : HK意境
 * @ClassName : SPIServiceImpl
 * @date : 2023/6/17 21:09
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SPIClass
public class SPIServiceImpl implements SPIService {

    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
