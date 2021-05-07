package com.demo.spring.simulation.v5.test.aop;

/**
 * 定义切面
 */
public class LogAspect {
    public void before() {
        System.out.println("Invoke Before Method.");
    }

    public void after() {
        System.out.println("Invoke After Method.");
    }

    public void afterThrowing() {
        System.out.println("Invoke Exception Handler.");
    }
}