package com.demo.spring.simulation.v5.test.ioc;

import com.demo.spring.simulation.v5.context.MyApplicationContext;
import com.demo.spring.simulation.v5.test.ioc.DemoController;

/**
 * Spring IoC和DI测试类。
 */
public class MyApplicationContextTest {

    public static void main(String[] args) {
        MyApplicationContext applicationContext = new MyApplicationContext("classpath:application.properties");
        // IoC容器初始化时，通过执行getBean完成DI。此处再次调用getBean防止有未被注入的属性。
        DemoController demoController = (DemoController) applicationContext.getBean(DemoController.class);
        demoController.say();
    }
}