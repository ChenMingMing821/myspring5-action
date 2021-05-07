package com.demo.spring.simulation.v5.test.ioc;

import com.demo.spring.simulation.v5.annotation.MyService;

@MyService
public class DemoService {
    public void say() {
        System.out.println("执行自定义Service方法。");
    }
}
