package com.hk.rpc.demo.spring.boot.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author : HK意境
 * @ClassName : SpringBootDemoConsumerApplication
 * @date : 2023/7/14 10:39
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SpringBootApplication
@ComponentScan("com.hk.rpc")
public class SpringBootDemoConsumerApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringBootDemoConsumerApplication.class, args);

    }


}
