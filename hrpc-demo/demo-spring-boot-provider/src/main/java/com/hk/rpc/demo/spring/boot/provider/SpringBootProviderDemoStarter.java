package com.hk.rpc.demo.spring.boot.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author : HK意境
 * @ClassName : SpringBootProviderDemoStarter
 * @date : 2023/7/14 9:07
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SpringBootApplication
@ComponentScan("com.hk.rpc")
public class SpringBootProviderDemoStarter {

    public static void main(String[] args) {

        SpringApplication.run(SpringBootProviderDemoStarter.class, args);
    }


}
