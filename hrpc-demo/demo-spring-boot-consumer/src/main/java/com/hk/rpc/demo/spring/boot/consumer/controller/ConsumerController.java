package com.hk.rpc.demo.spring.boot.consumer.controller;

import com.hk.rpc.demo.spring.boot.consumer.service.ConsumerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : HK意境
 * @ClassName : ConsumerController
 * @date : 2023/7/14 10:48
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Resource
    private ConsumerService consumerService;

    public String sayHello(@RequestParam("name") String name) {

        return consumerService.hello(name);
    }


}
