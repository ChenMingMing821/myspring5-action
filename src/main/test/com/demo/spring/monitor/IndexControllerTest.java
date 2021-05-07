package com.demo.spring.monitor;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

class IndexControllerTest {

    @Test
    public void test() {
//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
//        IndexService bean = (IndexService) applicationContext.getBean("index");
//        bean.index();
    }

    @Test
    public void test2() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Object bean = applicationContext.getBean("injection");
    }
}