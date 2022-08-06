package com.imooc.netty.websocket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HanBin_Yang
 * @since 2022/8/6 18:22
 */
@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping("/sayHello")
    public String sayHello() {
        return "hello, welcome to view springboot";
    }
}
