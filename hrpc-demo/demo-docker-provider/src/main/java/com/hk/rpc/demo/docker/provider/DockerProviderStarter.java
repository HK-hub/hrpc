package com.hk.rpc.demo.docker.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author : HK意境
 * @ClassName : DockerProviderStarter
 * @date : 2023/7/14 15:10
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SpringBootApplication
@ComponentScan("com.hk.rpc")
public class DockerProviderStarter {

    public static void main(String[] args) {

        SpringApplication.run(DockerProviderStarter.class, args);
    }


}
