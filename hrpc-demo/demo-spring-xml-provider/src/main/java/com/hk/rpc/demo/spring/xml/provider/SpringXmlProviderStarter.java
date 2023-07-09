package com.hk.rpc.demo.spring.xml.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author : HK意境
 * @ClassName : SpringXmlProviderStarter
 * @date : 2023/7/9 22:03
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class SpringXmlProviderStarter {

    public static void main(String[] args) {

        new ClassPathXmlApplicationContext("server-spring.xml");
    }

}
