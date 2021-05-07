package com.demo.spring.simulation.v5.aop.aspect;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * 通知定义接口，用于通知回调。
 */
@Data public class MyAdvice {
    // 切面实例化对象
    private Object aspect;
    // AOP方法，非目标方法
    private Method adviceMethod;
    // 针对异常处理时，异常的名称
    private String throwName;

    public MyAdvice(Object aspect, Method adviceMethod) {
        this.adviceMethod = adviceMethod;
        this.aspect = aspect;
    }
}
