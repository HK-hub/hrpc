package com.hk.rpc.test.spi;

import com.hk.rpc.spi.loader.ExtensionLoader;
import com.hk.rpc.test.spi.service.SPIService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author : HK意境
 * @ClassName : SPITest
 * @date : 2023/6/17 21:13
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class SPITest {

    @Test
    public void testSPILoader() throws Exception {

        SPIService spiService = ExtensionLoader.getExtension(SPIService.class, "spiService");

        String hello = spiService.hello(" I am SPI");
        log.info("spi test resualt:{}", hello);
    }


}
