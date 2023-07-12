package com.hk.rpc.demo.spring.xml.consumer;

import com.hk.rpc.consumer.RpcClient;
import com.hk.rpc.demo.api.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author : HK意境
 * @ClassName : SpringXmlConsumerTest
 * @date : 2023/7/11 21:59
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class SpringXmlConsumerTest {

    @Autowired
    private RpcClient rpcClient;

    @Test
    public void testInterfaceRpc() throws Exception {

        DemoService demoService = this.rpcClient.create(DemoService.class);

        String res = demoService.hello("hrpc");
        log.info("调用rpc方法返回结果:{}", res);

    }


}
