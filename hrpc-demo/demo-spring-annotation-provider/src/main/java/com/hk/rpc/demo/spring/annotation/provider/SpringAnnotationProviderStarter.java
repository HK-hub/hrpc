package com.hk.rpc.demo.spring.annotation.provider;

import com.hk.rpc.demo.spring.annotation.provider.config.SpringAnnotationProviderConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author : HK意境
 * @ClassName : SpringAnnotationProviderStarter
 * @date : 2023/7/10 15:27
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class SpringAnnotationProviderStarter {

    public static void main(String[] args) {

        new AnnotationConfigApplicationContext(SpringAnnotationProviderConfig.class);
    }

}
