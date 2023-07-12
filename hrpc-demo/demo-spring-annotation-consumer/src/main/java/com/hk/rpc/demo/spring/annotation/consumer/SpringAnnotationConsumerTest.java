package com.hk.rpc.demo.spring.annotation.consumer;

import com.hk.rpc.demo.spring.annotation.consumer.config.SpringAnnotationConsumerConfig;
import com.hk.rpc.demo.spring.annotation.consumer.service.ConsumerDemoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author : HK意境
 * @ClassName : SpringAnnotationConsumerTest
 * @date : 2023/7/12 21:42
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class SpringAnnotationConsumerTest {

    @Test
    public void testInterfaces() throws Exception {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringAnnotationConsumerConfig.class);

        ConsumerDemoService demoService = applicationContext.getBean(ConsumerDemoService.class);

        String result = demoService.hello("HK意境");
        log.info("rpc 调用结果：{}", result);
    }


}
