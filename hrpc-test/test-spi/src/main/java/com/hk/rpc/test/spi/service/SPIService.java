package com.hk.rpc.test.spi.service;

import com.hk.rpc.spi.annotation.SPI;

/**
 * @author : HK意境
 * @ClassName : SPIService
 * @date : 2023/6/17 21:08
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SPI("spiService")
public interface SPIService {

    String hello(String name);

}
