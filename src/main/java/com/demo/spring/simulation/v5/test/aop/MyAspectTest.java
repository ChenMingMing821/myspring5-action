package com.demo.spring.simulation.v5.test.aop;

import com.demo.spring.simulation.v5.aop.MyAopProxy;
import com.demo.spring.simulation.v5.context.MyApplicationContext;

/**
 * Spring MVC测试。
 */
public class MyAspectTest {

    public static void main(String[] args) {
        MyApplicationContext applicationContext = new MyApplicationContext("classpath:application.properties");
        MyAopProxy aspectService = (MyAopProxy)applicationContext.getBean(AspectServiceImpl.class);
        aspectService.print();
    }
}