package com.demo.spring.simulation.v5.test.ioc;

import com.demo.spring.simulation.v5.annotation.MyAutowired;
import com.demo.spring.simulation.v5.annotation.MyController;

@MyController
public class DemoController {

    @MyAutowired()
    private DemoService demoService;

    public void say() {
        demoService.say();
    }
}
